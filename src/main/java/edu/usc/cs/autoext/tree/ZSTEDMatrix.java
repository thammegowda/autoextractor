package edu.usc.cs.autoext.tree;

import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Edit distance matrix used for computing minimum edit distance using
 * Zhang Shasha's algorithm for Tree Edit Distance (ZSTED)
 <div>
 *<h2>References</h2>
 *  <p>
 *  <a href="http://dl.acm.org/citation.cfm?id=76082">K. Zhang and D. Shasha. 1989.
 *  Simple fast algorithms for the editing distance between trees and related problems.
 *  SIAM J. Comput. 18, 6 (December 1989), 1245-1262. DOI=http://dx.doi.org/10.1137/0218082</a>
 * </p>
 * </pre>
 * </div>
 */
class ZSTEDMatrix {

    //TODO: make the costs configurable
    private int insertionCost = 1;
    private int deletionCost = 1;
    private int replaceCost = 1;
    private int noEditCost = 0;

    private Node tree1Root;
    private Node tree2Root;
    private List<Node> tree1Nodes;
    private List<Node> tree2Nodes;
    private int nRows;
    private int nCols;
    private int[][] matrix;
    private boolean computed;
    private DOMUtil domHelper;

    /**
     * Creates a matrix for given trees
     * @param tree1 first tree
     * @param tree2 second tree
     */
    public ZSTEDMatrix(Node tree1, Node tree2) {
        this.tree1Root = tree1;
        this.tree2Root = tree2;
        this.domHelper = new DOMUtil();

        Set<Short> types = new HashSet<>();
        types.add(Node.ELEMENT_NODE);
        this.tree1Nodes = domHelper.postOrderTraverse(tree1, types);
        this.tree2Nodes = domHelper.postOrderTraverse(tree2, types);
        Node empty = null; //FIXME: dont rely on null
        this.tree1Nodes.add(0, empty);
        this.tree2Nodes.add(0, empty);
        this.nRows = this.tree1Nodes.size();
        this.nCols = this.tree2Nodes.size();
        // Note: Tree1 nodes as rows and Tree2 nodes as columns
    }

    /**
     * Initializes the matrix and creates base cases
     */
    public void initialize(){
        this.matrix = new int[nRows][nCols];
        //Lemma 3.1 : forestdist(Φ,Φ)= 0;
        matrix[0][0] = noEditCost;

        //Lemma 3.2 : Tree1 v/s empty Tree2 => insert all the nodes
        for (int i = 1; i < nRows; i++){
            matrix[i][0] = matrix[i-1][0] + insertionCost;
        }

        // Lemma 3.3 : Empty Tree1 v/s Tree2 => Delete all the tree2 nodes
        for (int i = 1; i < nCols; i++) {
            matrix[0][i] = matrix[0][i-1] + deletionCost;
        }
    }

    /**
     * Computes the edit distance between trees
     * @return : minimum edit distance between root of two trees
     */
    public int compute(){
        this.initialize();
        Node tree1Low = domHelper.getLowestLeftNode(tree1Root);
        Node tree2Low = domHelper.getLowestLeftNode(tree2Root);

        for (int i = 1; i < nRows; i++) {
            Node i1 = tree1Nodes.get(i);
            Node i1Low = domHelper.getLowestLeftNode(i1);

            for (int j = 1; j < nCols; j++) {
                Node j1 = tree2Nodes.get(j);
                Node j1Low = domHelper.getLowestLeftNode(j1);
                int del = matrix[i][j-1] + deletionCost;
                int insert = matrix[i-1][j] + insertionCost;

                int replacementCost = matrix[i-1][j-1];
                if (i1.getNodeName().equals(j1.getNodeName())) {
                    // no edit required
                    replacementCost += this.noEditCost;
                } else {
                    //replacement is required
                    //check if we need a single node replacement or a subtree replacement
                    if (i1Low.getNodeName().equals(tree1Low.getNodeName())
                            && j1Low.getNodeName().equals(tree2Low.getNodeName())) {
                        //simple Replace, just a single node
                        replacementCost += this.replaceCost;
                    } else {
                        //sub tree replace => replace the subtree rooted at i with j
                        //TODO: reuse existing matrix, #DP
                        //replacementCost += 2;
                        //FIXME:Dynamic programming please. The time will increase exponentially
                        replacementCost += new ZSTEDMatrix(i1, j1).compute();
                    }
                }
                //Minimum of three cases (Insert, Delete, Replace)
                matrix[i][j] = Math.min(insert, Math.min(del, replacementCost));
            }
        }
        this.computed = true;
        return matrix[nRows-1][nCols-1];
    }

    /**
     * prints a nicely formatted matrix to STDOUT
     */
    public void prettyPrint(){
        System.out.printf("Matrix %dx%d; Min EdDist=%d\n\t",
                nRows, nCols, getMinEdDistance());
        for (Node node : tree2Nodes) {
            System.out.printf("'%s'\t", node == null ? null :node.getNodeName());
        }
        System.out.println();
        for (int i = 0; i < nRows; i++) {
            System.out.printf("'%s'\t", tree1Nodes.get(i) == null ? null : tree1Nodes.get(i).getNodeName());
            for (int j = 0; j < nCols; j++) {
                System.out.printf("%2d\t", matrix[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Retrieves minimum edit distance between root nodes of the trees
     * @return minimum edit distance between root  notes of trees
     */
    public int getMinEdDistance(){
        if (!computed) {
            compute();
        }
        return matrix[nRows - 1][nCols - 1];
    }
}
