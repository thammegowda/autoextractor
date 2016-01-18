package edu.usc.irds.autoext.spark;

import org.apache.hadoop.mapred.lib.MultipleSequenceFileOutputFormat;

/**
 * OutputFormat that can write records to different groups based on keys
 * @param <K> the key type
 * @param <V> the value type
 */
public class RDDMultipleOutputFormat<K, V> extends MultipleSequenceFileOutputFormat<K,V> {

    @Override
    protected String generateFileNameForKeyValue(K key, V value, String name) {
        return key.toString();
    }
}
