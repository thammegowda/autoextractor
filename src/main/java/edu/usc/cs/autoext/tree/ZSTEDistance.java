package edu.usc.cs.autoext.tree;

import edu.usc.cs.autoext.base.EditCost;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Zhang - Shasha's Tree edit distance matrix
 * @see ZSTEDComputer
 */
public class ZSTEDistance {

    private final List<TreeNode> iNodes;
    private final List<TreeNode> jNodes;
    private final List<Integer> iKeyRoots;
    private final List<Integer> jKeyRoots;
    private int[] iLs;
    private int[] jLs;
    private double[][] treeDist;
    private final EditCost cost;

    public ZSTEDistance(TreeNode iTree, TreeNode jTree){
        this(iTree, jTree, new DefaultEditCost());
    }

    /**
     * Creates a Edit distance matrix for given trees
     * @param iTree root node of first tree
     * @param jTree root node of second tree
     * @param cost costs for edit operations
     */
    public ZSTEDistance(TreeNode iTree, TreeNode jTree, EditCost cost){
        this.cost = cost;
        this.iNodes = iTree.postOrderTraverse();
        this.jNodes = jTree.postOrderTraverse();
        this.treeDist = new double[iNodes.size()][jNodes.size()];
        this.iKeyRoots = iTree.getKeyRoots().stream().map(TreeNode::getIndex).collect(Collectors.toList());
        this.jKeyRoots = jTree.getKeyRoots().stream().map(TreeNode::getIndex).collect(Collectors.toList());
    }

    /**
     * Computes and returns edit distance
     * @return min edit distance between trees
     */
    public double compute(){
        this.iLs = new int[iNodes.size()];
        this.jLs = new int[jNodes.size()];
        for (int i = 0; i < iNodes.size(); i++) {
            this.iLs[i] = iNodes.get(i).getLeftMostDescendant().getIndex();
        }
        for (int i = 0; i < jNodes.size(); i++) {
            this.jLs[i] = jNodes.get(i).getLeftMostDescendant().getIndex();
        }
        for (Integer i : iKeyRoots) {
            for (Integer j : jKeyRoots) {
                treeDistance(i, j);
            }
        }
        return this.treeDist[iNodes.size() -1][jNodes.size() -1];
    }

    /**
     * Computes Tree distance between ith node and jth node
     * @param i index of first node
     * @param j index of second node
     */
    private void treeDistance(int i, int j){
        int m = i - iLs[i] + 2;
        int n = j - jLs[j] + 2;
        double fd[][] = new double[m][n];

        int iOffset = iLs[i] - 1;
        int jOffset = jLs[j] - 1;

        for (int x = 1; x < m; x++) {
            // δ(l(i1)..i, θ) = δ(l(1i)..1-1, θ) + γ(v → λ)
            fd[x][0] = fd[x-1][0] + cost.getRemoveCost(iNodes.get(x+iOffset));
        }
        for (int y = 1; y < n; y++) {
            //# δ(θ, l(j1)..j) = δ(θ, l(j1)..j-1) + γ(λ → w)
            fd[0][y] = fd[0][y-1] + cost.getInsertCost(jNodes.get(y+jOffset));
        }

        for (int x = 1; x < m; x++) {
            TreeNode i1 = iNodes.get(x + iOffset);
            for (int y = 1; y < n; y++) {
                TreeNode j1 = jNodes.get(y + jOffset);
                double removeCost = fd[x - 1][y] + cost.getRemoveCost(i1);
                double insertCost = fd[x][y - 1] + cost.getInsertCost(j1);

                // only need to check if x is an ancestor of i
                // and y is an ancestor of j
                if (iLs[i] == iLs[x+iOffset] && jLs[j] == jLs[y+jOffset]){
                    double replacementCost = fd[x - 1][y - 1] + (i1.getNodeName().equals(j1.getNodeName()) ? cost.getNoEditCost() : cost.getReplaceCost(i1, j1));
                    fd[x][y] = min(min(removeCost, insertCost), replacementCost);
                    treeDist[x+iOffset][y+jOffset] = fd[x][y];
                } else {
                    int p = iLs[x+iOffset]-1-iOffset;
                    int q = jLs[y+jOffset]-1-jOffset;
                    fd[x][y] = min(min(removeCost, insertCost), fd[p][q] + treeDist[x+iOffset][y+jOffset]);
                }
            }
        }
    }
}
