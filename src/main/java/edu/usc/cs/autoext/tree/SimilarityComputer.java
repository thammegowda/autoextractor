package edu.usc.cs.autoext.tree;

import java.util.List;

/**
 * Created by tg on 1/4/16.
 */
public class SimilarityComputer {

    private EditDistanceComputer<TreeNode> distanceComputer;
    private double maxUnitCost;

    public SimilarityComputer(EditDistanceComputer<TreeNode> distanceComputer) {
        this.distanceComputer = distanceComputer;
        this.maxUnitCost = distanceComputer.getCostMetric().getMaxUnitCost();
    }

    /**
     * Computes similarity between the trees using edit distance measure
     * @param tree1 first tree
     * @param tree2 second tree
     * @return similarity measure
     */
    public double computeSimilarity(TreeNode tree1, TreeNode tree2){
        return computeSimilarity(distanceComputer.computeDistance(tree1, tree2),
                tree1.getSize(), tree2.getSize());
    }

    /**
     * Computes similarity between the trees using edit distance measure
     * @param distance first distance
     * @param size1 number of elements in first tree
     * @param size2 number of elements in second tree
     * @return similarity measure
     */
    public double computeSimilarity(double distance, int size1, int size2){
        //Wish I could speak java here instead of maths :-)
        return 1.0 - distance/(distanceComputer.getCostMetric().getMaxUnitCost() * (size1 + size2));
    }
    /**
     * Computes similarity matrix
     * @param trees list of trees
     * @return similarity matrix
     */
    public double[][] compute(List<TreeNode> trees) {
        int n = trees.size();
        if (n < 2) {
            throw new IllegalArgumentException("At least two nodes should be given");
        }
        double matrix[][] = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = computeSimilarity(trees.get(i), trees.get(j));
            }
        }
        return matrix;
    }
}
