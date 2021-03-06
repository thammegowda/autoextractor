package edu.usc.irds.autoext.tree;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by tg on 12/29/15.
 */
public class ZSTEDComputerTest {

    @Test
    public void testMain() throws Exception {
        ClassLoader resLoader = getClass().getClassLoader();
        String file1 = resLoader.getResource("html/simple/1.html").getPath();
        String file2 = resLoader.getResource("html/simple/2.html").getPath();
        String file3 = resLoader.getResource("html/simple/3.html").getPath();
        double distance;
        //same file
        distance = ZSTEDComputer.computeDistance(new File(file1), new File(file1));
        assertEquals(0.0, distance, 0.00);

        //almost same
        distance = ZSTEDComputer.computeDistance(new File(file1), new File(file2));
        assertEquals(3.0, distance, 0.00);
        //if(true) return;
        //dissimilar
        distance = ZSTEDComputer.computeDistance(new File(file1), new File(file3));
        assertEquals(10.0, distance, 0.00);

    }
}