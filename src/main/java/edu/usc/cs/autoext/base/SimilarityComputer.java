package edu.usc.cs.autoext.base;

import edu.usc.cs.autoext.tree.StructureSimComputer;
import edu.usc.cs.autoext.tree.StyleSimComputer;

/**
 * Generic Similarity computer contract. Look into the implementations for specific details
 * @see StructureSimComputer
 * @see  StyleSimComputer
 * @author Thamme Gowda
 *
 */
public interface SimilarityComputer<T> {

    /**
     * computes similarity between two objects. The similarity score is on [0.0, 1.0] scale inclusive.
     * The score of 1.0 indicates that argument {@code obj1} and {@code obj2} are extremely similar.
     * Similarity score of 0.0 indicates that both input objects are extremely dissimilar.
     * @param obj1 the first object
     * @param obj2  the second object
     * @return the similarity score [0.0, 1.0]
     */
    double compute(T obj1, T obj2);
}
