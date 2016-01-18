package edu.usc.irds.autoext.nutch;

import org.apache.nutch.protocol.Content;
import scala.collection.AbstractIterator;

import java.util.Iterator;

/**
 * This iterator wraps iterator by adding numeric index to content
 * @author Thamme Gowda
 */
public class IndexedContentIterator extends AbstractIterator<IndexedContent>
        implements java.util.Iterator<IndexedContent>,
        scala.collection.Iterator<IndexedContent> {

    private Iterator<Content> iterator;
    private long index;

    /**
     * Creates indexed iterator starting from index 0
     * @param iterator the inner iterator
     */
    public IndexedContentIterator(Iterator<Content> iterator) {
        this(iterator, 0);
    }
    /**
     * Creates indexed iterator
     * @param iterator the inner iterator
     * @param startIndex the starting index
     */
    public IndexedContentIterator(Iterator<Content> iterator, long startIndex) {
        super();
        this.index = startIndex;
        this.iterator = iterator;
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IndexedContent next() {
        return new IndexedContent(index++, iterator.next());
    }
}
