package edu.usc.cs.autoext.tree;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Created by tg on 12/30/15.
 */
public class EditDistance {

    private int remove_cost = 1;
    private int insert_cost = 1;
    private int update_cost = 1;
    private int noedit_cost = 0;
    private final List<TreeNode> iNodes;

    private final List<TreeNode> jNodes;
    private final int[] jLmds;
    private final int[] iLmds;
    private double[][] treedist;
    private final EditCost cost;

    public  double result;

    public EditDistance(TreeNode iTree, TreeNode jTree){
        this(iTree, jTree, new DefaultEditCost());
    }
    public EditDistance(TreeNode iTree, TreeNode jTree, EditCost cost){
        this.cost = cost;
        this.iNodes = iTree.postOrderTraverse();
        this.jNodes = jTree.postOrderTraverse();
        this.treedist = new double[iNodes.size()][jNodes.size()];
        List<Integer> list = iNodes.stream().map(f -> f.getLeftMostDescendant().getIndex()).collect(Collectors.toList());
        this.iLmds = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.iLmds[i] = list.get(i);
        }
        list = jNodes.stream().map(f -> f.getLeftMostDescendant().getIndex()).collect(Collectors.toList());
        this.jLmds = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.jLmds[i] = list.get(i);
        }
        List<Integer> Akrs = iTree.getKeyRoots().stream().map(TreeNode::getIndex).collect(Collectors.toList());
        List<Integer> Bkrs = jTree.getKeyRoots().stream().map(TreeNode::getIndex).collect(Collectors.toList());
        for (Integer i : Akrs) {
            for (Integer j : Bkrs) {
                treeDistance(i, j);
            }
        }
        this.result = this.treedist[iNodes.size() -1][jNodes.size() -1];
    }

    private void treeDistance(int i, int j){
        int m = i - iLmds[i] + 2;
        int n = j - jLmds[j] + 2;
        double fd[][] = new double[m][n];

        int ioff = iLmds[i] - 1;
        int joff = jLmds[j] - 1;

        for (int x = 1; x < m; x++) {
            // δ(l(i1)..i, θ) = δ(l(1i)..1-1, θ) + γ(v → λ)
            fd[x][0] = fd[x-1][0] + cost.getInsertCost(iNodes.get(x+ioff));
        }
        for (int y = 1; y < n; y++) {
            //# δ(θ, l(j1)..j) = δ(θ, l(j1)..j-1) + γ(λ → w)
            fd[0][y] = fd[0][y-1] + insert_cost;
        }

        for (int x = 1; x < m; x++) {
            for (int y = 1; y < n; y++) {
                // only need to check if x is an ancestor of i
                // and y is an ancestor of j
                if (iLmds[i] == iLmds[x+ioff] && jLmds[j] == jLmds[y+joff]){
                    fd[x][y] = min(min(fd[x-1][y] + remove_cost, fd[x][y-1] + insert_cost),
                            fd[x-1][y-1] + (iNodes.get(x+ioff).getNodeName().equals(jNodes.get(y+joff).getNodeName()) ? noedit_cost : update_cost));
                    treedist[x+ioff][y+joff] = fd[x][y];
                } else {
                    int p = iLmds[x+ioff]-1-ioff;
                    int q = jLmds[y+joff]-1-joff;
                    fd[x][y] = min(min(
                            fd[x-1][y] + remove_cost,
                            fd[x][y-1] + insert_cost),
                            fd[p][q] + treedist[x+ioff][y+joff]);
                }
            }
        }
    }
}
