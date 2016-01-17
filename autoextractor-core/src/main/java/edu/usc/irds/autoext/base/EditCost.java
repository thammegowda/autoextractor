package edu.usc.irds.autoext.base;

import edu.usc.irds.autoext.tree.DefaultEditCost;

/**
 * Defines contract for Edit cost used by edit cost computer
 * @see DefaultEditCost
 */
public interface EditCost<T> {

    /**
     * Cost for insertion operation
     * @param node node to be inserted
     * @return the cost of insertion
     */
    double getInsertCost(T node);

    /**
     * cost for remove operation
     * @param node node to be removed
     * @return cost for removal
     */
    double getRemoveCost(T node);

    /**
     * Cost for replacement
     * @param node1 node to be removed
     * @param node2 node to be inserted
     * @return cost for the replacement
     */
    double getReplaceCost(T node1, T node2);

    /**
     * Cost for no edit operation
     * @return cost for no operation
     */
    double getNoEditCost();


    /**
     * Maximum cost for any single edit operation.
     * @return maximum bound on unit edit cost
     */
    double getMaxUnitCost();


    /**
     * true if the edit costs are symmetry. Symmetrc
     * @return true or false based on the symmetric nature
     */
    boolean isSymmetric();
}
