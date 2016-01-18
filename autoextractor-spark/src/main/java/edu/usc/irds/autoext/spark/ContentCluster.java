package edu.usc.irds.autoext.spark;

import edu.usc.irds.autoext.nutch.NutchContentRDD;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.nutch.protocol.Content;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clusters Nutch Output
 */
public class ContentCluster {

    public static final Logger LOG = LoggerFactory.getLogger(ContentCluster.class);
    public static final String DOMAINS_DIR = "domains";

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
    }

    /**
     * Separates records by domains in input
      */
    private void separateDomains(){
        new NutchContentRDD(ctx.sc(), parts)
                .toJavaRDD()
                .mapToPair(c -> new Tuple2<>(new Text(new URL(c.getUrl()).getHost()), c))
                .saveAsHadoopFile(domainsDir.toString(),
                    Text.class, Content.class,
                    RDDMultipleOutputFormat.class);

    }

    private byte run(){
        LOG.info("Starting the job");
        // get input and separate by domains
        LOG.info("Step 1: Separating the domains");
        separateDomains();

        // similarity matrix => cartesian
        // cluster
        // output
        // report
        return 0;

    }

    public static void main(String[] args) throws IOException {

        //args = "-list list.txt -workdir out1".split(" ");
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
        System.exit(code);
    }
}

