package edu.usc.irds.autoext.nutch;

import org.apache.nutch.protocol.Content;

/**
 * Created by tg on 1/18/16.
 */
public class IndexedContent {
    private final long index;
    private final Content content;

    public IndexedContent(long index, Content content) {
        this.index = index;
        this.content = content;
    }

    public long getIndex() {
        return index;
    }

    public Content getContent() {
        return content;
    }
}
