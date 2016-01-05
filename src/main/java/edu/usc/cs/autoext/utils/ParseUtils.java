package edu.usc.cs.autoext.utils;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by tg on 1/5/16.
 */
public class ParseUtils {

    private static DOMParser domParser = new DOMParser();

    public static Document parseFile(String path) throws IOException, SAXException {
        synchronized (domParser) {
            domParser.parse(new InputSource(new FileInputStream(path)));
            Document document = domParser.getDocument();
            domParser.reset();
            return document;
        }
    }
}
