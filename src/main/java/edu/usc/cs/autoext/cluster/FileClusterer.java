package edu.usc.cs.autoext.cluster;

import edu.usc.cs.autoext.tree.TreeNode;
import edu.usc.cs.autoext.tree.ZSTEDComputer;
import edu.usc.cs.autoext.utils.ParseUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by tg on 1/5/16.
 */
public class FileClusterer {

    public static final Logger LOG = LoggerFactory.getLogger(FileClusterer.class);
    public static final String IDS_FILE = "ids.txt";
    public static final String ED_DIST_FILE = "edit-distance.csv";
    public static final String TREE_SIM_FILE = "tree-sim.csv";
    public static final String CLUSTER_FILE = "clusters.txt";
    public static final String REPORT_FILE = "report.txt";

    @Option(name = "-list",
            required = true,
            usage = "path to a file containing paths to html files that requires clustering")
    private File listFile;

    @Option(name = "-workdir",
            required = true,
            usage = "Path to directory to create intermediate files and reports")
    private  File workDir;

    public void cluster() throws IOException {

        Stream<String> paths = Files.lines(listFile.toPath())
                .map(String::trim)  //no spaces
                .filter(s -> !(s.isEmpty() || s.startsWith("#")));// no empty lines and no comment lines

        List<TreeNode> trees = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        paths.forEach(p -> {
            try {
                Document doc = ParseUtils.parseFile(p);
                TreeNode tree = new TreeNode(doc, null);
                tree.setExternalId(p);
                ids.add(p);
                trees.add(tree);
            } catch (IOException | SAXException e) {
                LOG.error("Skip : {}, reason:{}", p, e.getMessage());
            }
        });
        LOG.info("Work Directory :{}", workDir.getAbsolutePath());
        LOG.info("Created work directory ? {} ", workDir.mkdirs());

        //Step1: write ids/paths to separate file
        File idsFile = new File(workDir, IDS_FILE);
        Files.write(idsFile.toPath(), ids);
        LOG.info("Wrote paths to {} ", idsFile.toPath());

        //Step 2: write edit distances to CSV

        ZSTEDComputer edComputer = new ZSTEDComputer();
        //Step 3: write similarity to CSV
        //write cluster to a file

    }

    public static void main(String[] args) throws IOException {
        FileClusterer instance = new FileClusterer();
        CmdLineParser parser = new CmdLineParser(instance);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println(e.getLocalizedMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }
        instance.cluster();
    }
}
