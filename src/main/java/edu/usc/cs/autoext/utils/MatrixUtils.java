package edu.usc.cs.autoext.utils;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Utilities related to matrix operations
 */
public class MatrixUtils {

    /**
     * Computes the symmetrical matrix.
     * @param function the function that can be applied to a pair of objects and returns a double
     * @param objs list of objects
     * @param <T> the object type
     * @return 2D matrix computed by applying function on pairs of objects.
     */
    public static <T> double[][] computeSymmetricMatrix(BiFunction<T,T, Double> function, List<T> objs){
        int n = objs.size();
        double[][] table = new double[n][n];
        for (int i = 0; i < n; i++) {
            T objI = objs.get(i);
            table[i][i] = function.apply(objI, objI); // the principal diagonal element
            for (int j = i + 1; j < objs.size(); j++) {
                table[i][j] = function.apply(objI, objs.get(j)); // the upper diagonal
                table[j][i] = table[i][j]; // the lower diagonal
            }
        }
        return table;
    }


    /**
     * Prints the matrix to STDOUT
     * @param matrix the matrix
     */
    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%5.2f\t", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
