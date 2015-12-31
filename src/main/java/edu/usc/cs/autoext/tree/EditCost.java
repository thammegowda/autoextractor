package edu.usc.cs.autoext.tree;

/**
 * Defines contract for Edit cost used by edit cost computer
 * @see DefaultEditCost
 */
public interface EditCost {

    /**
     * Cost for insertion operation
     * @param node node to be inserted
     * @return the cost of insertion
     */
    double getInsertCost(TreeNode node);

    /**
     * cost for remove operation
     * @param node node to be removed
     * @return cost for removal
     */
    double getRemoveCost(TreeNode node);

    /**
     * Cost for replacement
     * @param node1 node to be removed
     * @param node2 node to be inserted
     * @return cost for the replacement
     */
    double getReplaceCost(TreeNode node1, TreeNode node2);

    /**
     * Cost for no edit operation
     * @return cost for no operation
     */
    double getNoEditCost();

}
