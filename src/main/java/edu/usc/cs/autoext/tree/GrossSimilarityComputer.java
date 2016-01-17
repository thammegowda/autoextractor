package edu.usc.cs.autoext.tree;

import edu.usc.cs.autoext.base.SimilarityComputer;
import edu.usc.cs.autoext.utils.Checks;

import java.util.List;

/**
 * Aggregates the similarities from several similarity computers
 * @author Thamme Gowda N
 * @since Jan 16, 2016
 */
public class GrossSimilarityComputer<T> implements SimilarityComputer<T> {

    private final List<SimilarityComputer<T>> computers;
    private final List<Double> weights;
    private int n;

    /**
     * Creates a similarity aggregator
     * @param computers list of similarity computers
     * @param weights list of weights to the computers.
     *                The weight at the index i in this array specifies the weight for similaritycomputer at i in the argument 1.
     *                The sum of all weights should add to 1.0
     */
    public GrossSimilarityComputer(List<SimilarityComputer<T>> computers,
                                   List<Double> weights) {
        this.computers = computers;
        this.weights = weights;
        Checks.check(computers.size() == weights.size(),
                "The size of computers and weights should match");
        double sum = 0.0;
        for (Double weight : weights) {
            sum += weight;
        }
        Checks.check(Math.abs(1.0 - sum) <= 0.001,
                "The sum of all the weights must add up to 1.0");
        this.n = weights.size();
    }

    @Override
    public double compute(T obj1, T obj2) {
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            result += computers.get(i).compute(obj1, obj2) * weights.get(i);
        }
        return result;
    }
}
