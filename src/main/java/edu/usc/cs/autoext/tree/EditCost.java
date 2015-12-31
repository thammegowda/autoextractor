package edu.usc.cs.autoext.tree;

/**
 * Created by tg on 12/30/15.
 */
public interface EditCost {
    double getInsertCost(TreeNode node);
    double getRemoveCost(TreeNode node);
    double getReplaceCost(TreeNode node1, TreeNode node2);
    double getNoEditCost();

    default double getCost(TreeNode node1, TreeNode node2) {

        if (node1 == null && node2 != null) {
            // insert node2
            return getInsertCost(node2);
        } else if (node1 != null && node2 == null) {
            //delete node1
            return getRemoveCost(node1);
        } else if ((node1 == null && node2 == null)
                || (node1.getNodeName().equals(node2.getNodeName()))){
            //no edit
            return getNoEditCost();
        } else {
            return getReplaceCost(node1, node2);
        }
    }
}
