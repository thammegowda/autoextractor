package edu.usc.irds.autoext.spark;

import edu.usc.irds.autoext.nutch.ContentIterator;
import edu.usc.irds.autoext.nutch.IndexedNutchContentRDD;
import edu.usc.irds.autoext.nutch.NutchContentRDD;
import edu.usc.irds.autoext.tree.GrossSimComputer;
import edu.usc.irds.autoext.tree.TreeNode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.nutch.protocol.Content;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.cyberneko.html.parsers.DOMParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import scala.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class Clusters Nutch's Output
 */
public class ContentCluster implements Closeable {

    public static final Logger LOG = LoggerFactory.getLogger(ContentCluster.class);
    public static final String DOMAINS_DIR = "domains";
    public static final String SIMILARITY_DIR = "similarity";

    @Option(name = "-list", required = true,
            usage = "List of Nutch Segment(s) Part(s)")
    private File pathsList;

    @Option(name="-master", usage = "Spark master url")
    private String masterUrl = "local[2]";

    @Option(name="-workdir", required = true,
            usage = "Work directory.")
    private String workDir;

    private JavaSparkContext ctx;
    private List<String> parts;
    private Path domainsDir;
    private Configuration hConf;
    private Path simDirectory;

    private void init() throws IOException {
        LOG.info("Initializing Spark..\n spark master={}", masterUrl);
        SparkConf conf = new SparkConf()
                .setAppName(getClass().getName())
                .setMaster(masterUrl);
        this.ctx = new JavaSparkContext(conf);

        LOG.info("Reading segments list from {}", pathsList);
        this.parts = Files.lines(pathsList.toPath())
                .map(String::trim)
                .filter(l -> !(l.isEmpty() || l.startsWith("#")))
                .collect(Collectors.toList());
        LOG.info("Found {} segment paths", parts.size());

        this.domainsDir = new Path(workDir, DOMAINS_DIR);
        this.simDirectory = new Path(workDir, SIMILARITY_DIR);
        this.hConf = new Configuration();
    }

    /**
     * Separates records by domains in input
     */
    private void separateDomains(){
        Function<String, Boolean> filter = (Function<String, Boolean> & Serializable) s -> s.contains("html");
        new NutchContentRDD(ctx.sc(), parts, filter)
                .toJavaRDD()
                .mapToPair(c -> new Tuple2<>(new Text(new URL(c.getUrl()).getHost()), c))
                .saveAsHadoopFile(domainsDir.toString(),
                        Text.class, Content.class,
                        RDDMultipleOutputFormat.class);
    }

    private void computeSimilarity() throws IOException {
        FileSystem fs = FileSystem.get(hConf);
        FileStatus[] files = fs.listStatus(domainsDir, path -> {
            String name = path.getName();
            return !(name.startsWith("_") || name.startsWith("."));
        });
        for (FileStatus file : files) {
            try {
                String path = file.getPath().toString();
                if (!file.isFile()) {
                    LOG.warn("Skipped : {}", path);
                    continue;
                }
                String name = file.getPath().getName();
                LOG.info("Starting {}", name);
                JavaRDD<Tuple2<Long, TreeNode>> treeRDD =
                        new IndexedNutchContentRDD(ctx.sc(), path, ContentIterator.ACCEPT_ALL_FILTER)
                                .toJavaRDD()
                                .map(tuple -> {
                                    Content content = tuple.getContent();
                                    Document doc;
                                    try (ByteArrayInputStream stream =
                                                 new ByteArrayInputStream(content.getContent())) {
                                        InputSource source = new InputSource(stream);
                                        DOMParser parser = new DOMParser();
                                        parser.parse(source);
                                        doc = parser.getDocument();
                                    }
                                    try {
                                        TreeNode tree = new TreeNode(doc.getDocumentElement(), null);
                                        tree.setExternalId(content.getUrl());
                                        return new Tuple2<>(tuple.getIndex(), tree);
                                    } catch (Exception e) {
                                        LOG.error("URL={}", content.getUrl());
                                        LOG.error(e.getMessage(), e);
                                        return null;
                                    }
                                });
                treeRDD = treeRDD.filter(f -> f != null).cache();
                JavaPairRDD<Tuple2<Long, TreeNode>, Tuple2<Long, TreeNode>> pair = treeRDD.cartesian(treeRDD);

                //symmetric matrix => drop lower diagonal elements
                pair = pair.filter(tup -> tup._1()._1() >= tup._2()._1());

                JavaRDD<MatrixEntry> entryRDD = pair.flatMap(tup ->
                {
                    Long i = tup._1()._1();
                    Long j = tup._2()._1();
                    List<MatrixEntry> entries = new ArrayList<>();
                    if (Objects.equals(i, j)) {
                        //principal diagonal => same tree
                        entries.add(new MatrixEntry(i, j, 1.0));
                    } else {
                        TreeNode treeI = tup._1()._2();
                        TreeNode treeJ = tup._2()._2();
                        GrossSimComputer<TreeNode> computer = GrossSimComputer.createWebSimilarityComputer(0.75);
                        double score = computer.compute(treeI, treeJ);
                        entries.add(new MatrixEntry(i, j, score));
                        //symmetry
                        entries.add(new MatrixEntry(j, i, score));
                    }
                    return entries;
                });
                CoordinateMatrix similarityMatrix = new CoordinateMatrix(entryRDD.rdd());
                Path outPath = new Path(simDirectory, name);
                similarityMatrix
                        .toIndexedRowMatrix()
                        .rows()
                        .saveAsTextFile(outPath.toString());
            } catch (Exception e) {
                LOG.error("Failed : {}", file);
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private byte run() throws IOException {
        LOG.info("Starting the job");

        //1 & 2: get input and separate by domains
        LOG.info("Step 1 and 2: Separating the domains");
        separateDomains();

        //3: Compute similarity matrix
        LOG.info("Step 3: Computing similarity");
        computeSimilarity();

        // cluster
        // output
        // report
        return 0;

    }


    @Override
    public void close() throws IOException {
        if (ctx != null) {
            ctx.close();
        }
    }

    public static void main(String[] args) throws IOException {

        //args = "-list list.txt -workdir out2".split(" ");
        ContentCluster contentCluster = new ContentCluster();
        CmdLineParser parser = new CmdLineParser(contentCluster);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }

        contentCluster.init();
        int code = contentCluster.run();

        contentCluster.close();
        System.exit(code);
    }
}

