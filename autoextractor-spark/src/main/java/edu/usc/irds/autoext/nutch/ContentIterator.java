package edu.usc.irds.autoext.nutch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.AbstractIterator;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Reads Nutch Content from Sequence files
 * @author Thamme Gowda
 */
public class ContentIterator extends AbstractIterator<Content> implements Iterator<Content> {

    public static final Logger LOG = LoggerFactory.getLogger(ContentIterator.class);
    public static final Function<String, Boolean> ACCEPT_ALL_FILTER =
            (Serializable & Function<String, Boolean>) contentType -> true;

    private long count;
    private final SequenceFile.Reader reader;
    private final Writable key;
    private Content next;
    private Function<String, Boolean> contentTypeFilter;

    /**
     *
     * @param path the path to sequence file
     * @param conf Hadoop HDFS configuration
     * @param contentFilter content type filter
     * @throws IOException when an io error occurs
     */
    public ContentIterator(Path path, Configuration conf, Function<String, Boolean> contentFilter) throws IOException {
        super();
        this.reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
        this.key = ReflectionUtils.newInstance((Class<? extends Writable>) reader.getKeyClass(), conf);
        this.contentTypeFilter = contentFilter;
        this.next = makeNext();
    }

    public ContentIterator(Path path, Configuration conf) throws IOException {
        this(path, conf, ACCEPT_ALL_FILTER);
    }

    protected Content makeNext() {
        Content value = new Content();
        try {
            while (reader.next(key, value)) {
                if (contentTypeFilter.apply(value.getContentType())) {
                    count++;
                    return value;
                } // else skip
            }
            LOG.debug("Reached the end after {} records", count);
            IOUtils.closeStream(reader);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            IOUtils.closeStream(reader);
        }
        //END of the stream
        return null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Content next() {
        Content tmp = next;
        next = makeNext();
        return tmp;
    }
}
