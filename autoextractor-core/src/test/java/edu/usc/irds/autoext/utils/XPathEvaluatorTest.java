package edu.usc.irds.autoext.utils;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tg on 1/16/16.
 */
public class XPathEvaluatorTest {

    XPathEvaluator instance = new XPathEvaluator();
    Element docRoot ;
    {
        try {
            DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            docRoot = b.parse(getClass().getClassLoader()
                    .getResourceAsStream("html/simple/1.html")).getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEval() throws Exception {
        XPathExpression titleExpr = instance.compile("//title/text()");
        NodeList list = instance.eval(docRoot, titleExpr);
        assertEquals(1, list.getLength());
        assertEquals("This is my page 1", list.item(0).getTextContent());
    }

    @Test
    public void testFindUniqueClassNames() throws Exception {
        Set<String> names = instance.findUniqueClassNames(docRoot);
        assertEquals(6, names.size());
        assertTrue(names.contains("header"));
        assertTrue(names.contains("row"));
        assertTrue(names.contains("cell"));
        assertTrue(names.contains("col1"));
        assertTrue(names.contains("col2"));
        assertTrue(names.contains("table"));
    }
}