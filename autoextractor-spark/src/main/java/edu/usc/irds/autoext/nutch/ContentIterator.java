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
import scala.collection.Iterator;

import java.io.IOException;

/**
 * Reads Nutch Content from Sequence files
 * @author Thamme Gowda
 */
public class ContentIterator extends AbstractIterator<Content> implements Iterator<Content> {

    public static final Logger LOG = LoggerFactory.getLogger(ContentIterator.class);

    private long count;
    private final SequenceFile.Reader reader;
    private final Writable key;
    private Content next;

    public ContentIterator(Path path, Configuration conf) throws IOException {
        super();
        this.reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
        this.key = ReflectionUtils.newInstance((Class<? extends Writable>) reader.getKeyClass(), conf);
        this.next = makeNext();
    }

    protected Content makeNext() {
        Content value = new Content();
        try {
            if (reader.next(key, value)) {
                count++;
                return value;
            } else {
                LOG.debug("Reached the end after {} records", count);
                IOUtils.closeStream(reader);
            }
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
