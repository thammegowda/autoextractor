package edu.usc.irds.autoext.tree;

import edu.usc.irds.autoext.base.SimilarityComputer;
import edu.usc.irds.autoext.utils.Checks;

import java.util.Arrays;
import java.util.List;

/**
 * Aggregates the similarities from several similarity computers
 * @author Thamme Gowda N
 * @since Jan 16, 2016
 */
public class GrossSimComputer<T> implements SimilarityComputer<T> {

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
    public GrossSimComputer(List<SimilarityComputer<T>> computers,
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

    /**
     k* A factory method for creating similarity computer that aggregates structural and stylistic measures
     * @param structureSimWeight The fraction weight of weight for structural similarity.
     *                           The remaining fraction, i.e. (1 - weight), will be taken as weight for style similarity
     * @return the similarity computer that internally aggregates structure and style measures;
     */
    public static GrossSimComputer<TreeNode> createWebSimilarityComputer(double structureSimWeight){
        Checks.check(structureSimWeight <= 1.0 && structureSimWeight >= 0.0, "The weight should be in between [0.0, 1.0]");
        ZSTEDComputer edComputer = new ZSTEDComputer();
        StructureSimComputer structSimComputer = new StructureSimComputer(edComputer);
        StyleSimComputer styleSimComputer = new StyleSimComputer();
        List<SimilarityComputer<TreeNode>> similarityComputers = Arrays.asList(structSimComputer, styleSimComputer);
        List<Double> weights = Arrays.asList(structureSimWeight, 1.0 - structureSimWeight);
        return new GrossSimComputer<>(similarityComputers, weights);
    }
}
