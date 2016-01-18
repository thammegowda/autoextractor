package edu.usc.irds.autoext.nutch;

import edu.usc.irds.autoext.spark.utils.LangUtils;
import org.apache.hadoop.fs.Path;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Iterator;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassTag;

import java.util.function.Function;

/**
 * Creates Nutch Content RDD from a list of Sequence file paths
 * @author Thamme Gowda
 */
public class IndexedNutchContentRDD extends RDD<IndexedContent> {

    public static final Logger LOG = LoggerFactory.getLogger(IndexedNutchContentRDD.class);
    private final Function<String, Boolean> contentTypeFilter;
    private static final ClassTag<IndexedContent> CONTENT_TAG = LangUtils.getClassTag(IndexedContent.class);
    private final ContentPartition[] partitions;

    /**
     * Creates Nutch Content RDD
     * @param context the spark Context
     * @param path path to nutch segment content data
     */
    public IndexedNutchContentRDD(SparkContext context,
                                  String path,
                                  Function<String, Boolean> contentTypeFilter) {
        super(context, new ArrayBuffer<>(), CONTENT_TAG);
        this.partitions = new ContentPartition[] {
                new ContentPartition(0, path)};
        this.contentTypeFilter = contentTypeFilter;
    }

    @Override
    public Iterator<IndexedContent> compute(Partition split, TaskContext context) {
        try {
            Path path = new Path(partitions[split.index()].getPath());
            java.util.Iterator<Content> iterator = new ContentIterator(path, NutchConfiguration.create(), contentTypeFilter);
            return new IndexedContentIterator(iterator);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Partition[] getPartitions() {
        return this.partitions;
    }

    public JavaRDD<IndexedContent> toJavaRDD(){
        return new JavaRDD<>(this, CONTENT_TAG);
    }
}
