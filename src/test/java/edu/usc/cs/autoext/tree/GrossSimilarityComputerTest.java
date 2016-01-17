package edu.usc.cs.autoext.tree;

import edu.usc.cs.autoext.base.SimilarityComputer;
import org.junit.Test;


import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Created by tg on 1/16/16.
 */
public class GrossSimilarityComputerTest {

    @Test
    public void testCompute() throws Exception {

        SimilarityComputer<String> caseSensitiveComputer = (obj1, obj2) -> obj1.equals(obj2) ? 1.0 : 0.0;
        SimilarityComputer<String> caseInsensitiveComputer = (obj1, obj2) -> obj1.toLowerCase().equals(obj2.toLowerCase()) ? 1.0 : 0.0;

        GrossSimilarityComputer<String> computer = new GrossSimilarityComputer<>(Arrays.asList(caseSensitiveComputer, caseInsensitiveComputer), Arrays.asList(0.5, 0.5));
        assertEquals(1.0, computer.compute("abcd", "abcd"), 0.00001);
        assertEquals(0.5, computer.compute("abcd", "ABCD"), 0.00001);
        assertEquals(0.0, computer.compute("aaa", "bbbb"), 0.00001);
    }
}