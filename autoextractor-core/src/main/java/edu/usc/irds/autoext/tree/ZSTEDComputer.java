package edu.usc.irds.autoext.tree;

import edu.usc.irds.autoext.base.EditCost;
import edu.usc.irds.autoext.base.EditDistanceComputer;
import edu.usc.irds.autoext.utils.MatrixUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements Zhang-Shasha's Tree Edit Distance (ZS-TED) algorithm for computing the
 * edit distance between DOM trees.
 * Computes edit distance between two nodes in DOM tree.
 * @author Thamme Gowda
 *
 * <br/>
 * <h1>References :</h1>
 * <pre>
 *     K. Zhang and D. Shasha. 1989. Simple fast algorithms for the editing distance between trees and related problems. SIAM J. Comput. 18, 6 (December 1989), 1245-1262.
 * </pre>
 */
public class ZSTEDComputer implements EditDistanceComputer<TreeNode> {

    private EditCost<TreeNode> costMetric = new DefaultEditCost();

    /**
     * CLI argument specification
     */
    private static class CliArg {
        @Option(name = "-in1", forbids = {"-dir"}, depends = "-in2")
        private File html1;

        @Option(name = "-in2", forbids = {"-dir"}, depends = "-in1")
        private File html2;

        @Option(name = "-dir", forbids = {"-in1", "-in2"})
        private File inputDir;
    }

    @Override
    public double computeDistance(TreeNode tree1, TreeNode tree2) {
        return new ZSTEDistance(tree1, tree2, costMetric).compute();
    }

    @Override
    public EditCost<TreeNode> getCostMetric() {
        return costMetric;
    }

    /**
     * Computes edit distance between two html files
     * @param file1 first html file
     * @param file2 second html file
     * @return edit distance measure
     * @throws IOException when an error occurs
     * @throws SAXException when parser fails
     */
    public static double computeDistance(File file1, File file2)
            throws IOException, SAXException {
        DOMParser domParser = new DOMParser();
        domParser.parse(new InputSource(new FileReader(file1)));
        Document doc1 = domParser.getDocument();
        domParser.reset();
        domParser.parse(new InputSource(new FileReader(file2)));
        Document doc2 = domParser.getDocument();

        ZSTEDComputer computer = new ZSTEDComputer();
        return computer.computeDistance(new TreeNode(doc1, null), new TreeNode(doc2, null));
    }

    /**
     * Computes edit distances between trees
     * @param trees list of trees who's edit distance is to be computed
     * @return an nxn square matrix with edit distance measure
     */
    public double[][] computeDistanceMatrix(List<TreeNode> trees){

        int n = trees.size();
        double distanceMatrix[][] = new double[n][n];
        boolean symmetricMeasure = getCostMetric().isSymmetric();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j){
                    //diagonal, same file, distance is zero
                    distanceMatrix[i][j] = 0.0;
                } else if (symmetricMeasure && i > j) {
                    // lower diagonal and the measure is a symmetry
                    distanceMatrix[i][j] = distanceMatrix[j][i];
                } else {
                    // upper diagonal or unsymmetrical, compute it
                    distanceMatrix[i][j] = computeDistance(trees.get(i), trees.get(j));
                }
            }
        }
        return distanceMatrix;
    }

    /**
     * Computes the edit distance between files in a directory
     * @param inputDir directory of html pages
     * @throws IOException
     * @throws SAXException
     */
    private static void computeDistances(File inputDir) throws IOException, SAXException {

        File[] files = inputDir.listFiles();
        List<TreeNode> docs = new ArrayList<>();
        List<String> htmlPaths = new ArrayList<>();
        DOMParser parser = new DOMParser();
        for (File file : files) {
            if (!file.isFile()) {
                //skip
                continue;
            }
            try(FileReader reader = new FileReader(file)) {
                parser.parse(new InputSource(reader));
                htmlPaths.add(file.getAbsolutePath());
                docs.add(new TreeNode(parser.getDocument(), null));
                parser.reset();
            }
        }
        int n = docs.size();
        if (n < 2) {
            throw new RuntimeException("At least 2 html/xml files should be present in the input directory");
        }

        ZSTEDComputer edComputer = new ZSTEDComputer();
        StructureSimComputer simComputer = new StructureSimComputer(edComputer);
        double[][] distMatrix = edComputer.computeDistanceMatrix(docs);
        int treeSizes[] = new int[n];
        for (int i = 0; i < docs.size(); i++) {
            treeSizes[i] = docs.get(i).getSize();
        }
        double[][] simMatrix = simComputer.compute(treeSizes, distMatrix);

        System.out.println("#Index\tFile Path");
        for (int i = 0; i < htmlPaths.size(); i++) {
            System.out.println(i + "\t" + htmlPaths.get(i));
        }
        System.out.println("\n#Distance Matrix");
        MatrixUtils.printMatrix(distMatrix);

        System.out.println("\n#Similarity Matrix");
        MatrixUtils.printMatrix(simMatrix);
    }

    public static void main(String[] args) throws IOException, SAXException {
        //args = "-in1 src/test/resources/html/simple/1.html -in2 src/test/resources/html/simple/2.html".split(" ");
        //args = "-dir src/test/resources/html/simple/".split(" ");
        CliArg arg = new CliArg();
        CmdLineParser parser = new CmdLineParser(arg);
        try {
            parser.parseArgument(args);
            if (arg.inputDir == null && arg.html1 == null) {
                throw new CmdLineException(parser, "Either -dir or -in1 should be given");
            }
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }
        if (arg.inputDir != null) {
            computeDistances(arg.inputDir);
        } else {
            double distance = computeDistance(arg.html1, arg.html2);
            System.out.println("Distance=" + distance);
        }

    }
}
