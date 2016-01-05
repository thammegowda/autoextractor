package edu.usc.cs.autoext.cluster;

import edu.usc.cs.autoext.tree.SimilarityComputer;
import edu.usc.cs.autoext.tree.TreeNode;
import edu.usc.cs.autoext.tree.ZSTEDComputer;
import edu.usc.cs.autoext.utils.Checks;
import edu.usc.cs.autoext.utils.ParseUtils;
import edu.usc.cs.autoext.utils.Tuple2;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <h1>References : </h1>
 * <pre>
 * Jarvis, R.A.; Patrick, Edward A., "Clustering Using a Similarity Measure Based on Shared Near Neighbors," in Computers, IEEE Transactions on , vol.C-22, no.11, pp.1025-1034, Nov. 1973
 * </pre>
 */
public class Clusterer {

    public static Comparator<Tuple2<Double, Integer>> descendingComparator =
            (o1, o2) -> Double.compare(o2.pos0, o1.pos0);

    /**
     * Clusters documents
     * @param simMatrix similarity matrix, values in between [0.0 to 1.0] inclusive
     * @param labels labels for items in similarity matrix
     * @param simThreshold similarity threshold to treat that the items are similar, usually >= 0.8
     * @param k number of nearest neighbours to start with
     * @param kt threshold number of neighbours to put onto same cluster
     */
    public void cluster(double simMatrix[][], String[] labels, double simThreshold, int k, int kt){

        Checks.check(simMatrix.length == labels.length, "Couldn't match labels to similarity matrix ");
        Checks.check(kt < k, "threshold 'kt' should be <= 'k'");

        int n = labels.length;
        //int table[][] = new int[n][k];
        List<int[]> table = new LinkedList<>();
        //computing the table
        for (int i = 0; i < n; i++) {
            table.add(i, findNearestNeighbors(simMatrix[i], simThreshold, k));
        }

        List<BitSet> fastTable = new ArrayList<>();
        for (int[] ints : table) {
            BitSet bitSet = new BitSet();
            for (int i : ints) {
                bitSet.set(i);
            }
            fastTable.add(bitSet);
        }


        int maxIterations = 100;
        boolean shrinking = true;
        int iteration = 0;
        while(shrinking && iteration < maxIterations) {
            shrinking = false; // will be marked true when it happens
            iteration++;

            for (int i = 0; i < fastTable.size(); i++) {
                if (fastTable.get(i).cardinality() < kt) {
                    //not possible
                    continue;
                }
                for (int j = i + 1; j < fastTable.size(); j++) {

                    BitSet from = (BitSet) fastTable.get(i).clone();
                    BitSet to = fastTable.get(j);
                    if (to.cardinality() < kt) {
                        //not possible
                        continue;
                    }
                    from.and(to);

                    if (from.cardinality() >= kt) {
                        // threshold or more neighbors
                        shrinking = true;
                        // drop j
                        fastTable.remove(j);
                        // replace j's index with i's index everywhere else

                        for (int l = j ; l < fastTable.size(); l++) {
                            if (l == i) {
                                //except this! ith cluster should have an entry
                                continue;
                            }
                            BitSet set = fastTable.get(l);
                            if (set.get(j)) {
                                set.clear(j);
                                set.set(i);
                            }
                        }
                    }
                }
            }
        }
        for (BitSet set : fastTable) {
            System.out.println(set);
        }
    }

    public int[] findNearestNeighbors(double[] similarity, double simThreshold, int k){

        SortedSet<Tuple2<Double, Integer>> nearests = new TreeSet<>(descendingComparator);
        int n = similarity.length;
        // the given node itself will have 1.0 score which is the highest similarity,
        // so no need to add it at the zeroth position explicitly
        for (int i = 0; i < n; i++) {
            if (similarity[i] >= simThreshold) {
                nearests.add(new Tuple2<>(similarity[i], i));
            }
        }
        n = nearests.size();
        int nearestIndex[] = new int[n];
        int i = 0;
        for (Tuple2<Double, Integer> nearest : nearests) {
            nearestIndex[i++] = nearest.pos1;
        }
        return nearestIndex;
    }

    public static void main(String[] args) throws IOException, SAXException {
        String dir = "/home/tg/work/data/htmls/yellowpages/test2";
        String[] fileNames = new File(dir).list();
        File[] files = new File(dir).listFiles();

        SimilarityComputer computer = new SimilarityComputer(new ZSTEDComputer());
        List<TreeNode> nodes = new ArrayList<>();
        for (File file : files) {
            Document doc = ParseUtils.parseFile(file.getAbsolutePath());
            nodes.add(new TreeNode(doc, null));
        }
        System.out.println("Number of trees found :" + nodes.size());
        double[][] sims = computer.compute(nodes);
        Clusterer clusterer = new Clusterer();
        clusterer.cluster(sims, fileNames, 0.85, 6, 3);

    }
}
