package edu.usc.cs.autoext.tree;

import org.cyberneko.html.parsers.DOMParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class implements Zhang-Shasha's Tree Edit Distance (ZS-TED) algorithm for computing the
 * edit distance between DOM trees.
 * Computes edit distance between two nodes in DOM tree.
 * @author Thamme Gowda
 *
 * NOTE: it is work in progress, there are few todo's and fixme's in the code
 */
public class ZSTEDComputer implements EditDistanceComputer<Node> {

    @Override
    public double computeDistance(Node tree1, Node tree2) {
        return new ZSTEDMatrix(tree1, tree2).compute();
    }

    /**
     * CLI argument specification
     */
    private static class CliArg {
        @Option(name = "-in1")
        private File html1;

        @Option(name = "-in2")
        private File html2;

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
        return computer.computeDistance(doc1, doc2);
    }


    public static void main(String[] args) throws IOException, SAXException {
        CliArg arg = new CliArg();
        CmdLineParser parser = new CmdLineParser(arg);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            e.getParser().printUsage(System.out);
            System.exit(1);
        }
        double distance = computeDistance(arg.html1, arg.html2);
        System.out.println("Distance=" + distance);
    }
}
