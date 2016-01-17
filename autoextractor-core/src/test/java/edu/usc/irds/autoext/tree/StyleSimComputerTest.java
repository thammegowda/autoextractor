package edu.usc.irds.autoext.tree;

import edu.usc.irds.autoext.utils.ParseUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by tg on 1/16/16.
 */
public class StyleSimComputerTest {

    StyleSimComputer instance = new StyleSimComputer();

    @Test
    public void testCountIntersection() throws Exception {

        Set<Integer> a = new HashSet<>(Arrays.asList(1,2,3));
        Set<Integer> b = new HashSet<>(Arrays.asList(3, 4, 5));
        assertEquals(1, instance.countIntersection(a, b));
        b.clear();
        assertEquals(0, instance.countIntersection(a, b));
        b.addAll(a);
        assertEquals(3, instance.countIntersection(a, b));
    }

    @Test
    public void testCompute() throws Exception {
        Document doc1 = ParseUtils.parseFile("src/test/resources/html/simple/1.html");
        Document doc2 = ParseUtils.parseFile("src/test/resources/html/simple/2.html");
        Document doc3 = ParseUtils.parseFile("src/test/resources/html/simple/3.html");

        TreeNode tree1 = new TreeNode(doc1.getDocumentElement(), null);
        TreeNode tree2 = new TreeNode(doc2.getDocumentElement(), null);
        TreeNode tree3 = new TreeNode(doc3.getDocumentElement(), null);

        assertEquals(1.0, instance.compute(tree1, tree1), 0.001);
        assertEquals(1.0, instance.compute(tree2, tree2), 0.001);
        assertEquals(1.0, instance.compute(tree3, tree3), 0.001);
        assertEquals(instance.compute(tree1, tree2), instance.compute(tree2, tree1), 0.001);
        assertEquals(instance.compute(tree1, tree3), instance.compute(tree3, tree1), 0.001);
        assertEquals(instance.compute(tree2, tree3), instance.compute(tree3, tree2), 0.001);

        assertEquals(0.9, instance.compute(tree1, tree2), 0.25);
        assertEquals(0.0, instance.compute(tree1, tree3), 0.25);
        assertEquals(0.0, instance.compute(tree2, tree3), 0.25);

    }
}