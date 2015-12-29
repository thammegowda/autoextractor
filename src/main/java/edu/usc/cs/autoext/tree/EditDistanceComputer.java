package edu.usc.cs.autoext.tree;

/**
 * Defines a contract for edit distance computer
 *
 * @author Thamme Gowda
 */
public interface EditDistanceComputer<T> {

    /**
     * Computes edit distance between two similar objects
     * @param object1 the first object
     * @param object2 the second object
     * @return the edit distance measure
     */
    double computeDistance(T object1, T object2);
}
