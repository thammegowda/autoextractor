package edu.usc.irds.autoext.nutch;

import org.apache.spark.Partition;

import java.io.Serializable;

/**
 * RDD Partition mapping to Nutch Segment part
 */
public class ContentPartition implements Partition, Serializable {

    private final int index;
    private final String path;

    public ContentPartition(int index, String path) {
        this.index = index;
        this.path = path;
    }

    @Override
    public int index() {
        return index;
    }

    public String getPath() {
        return path;
    }
}
