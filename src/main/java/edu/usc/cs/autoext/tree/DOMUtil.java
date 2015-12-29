package edu.usc.cs.autoext.tree;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is a suit of utilities to deal with W3 DOM API
 * @author  Thamme Gowda
 */
public class DOMUtil {

    /**
     * Traverses the tree in post order
     * @param root the root node of the tree
     * @param nodeTypes set of node types to pick from the tree while traversing.
     *                  Passing <code>null</code> will pick all the nodes.
     * @param traversedNodes List of nodes to which the new nodes are to be appended
     */
    private void postOrderTraverse(Node root, Set<Short> nodeTypes,
                                   List<Node> traversedNodes){
        if (root.hasChildNodes()){
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                postOrderTraverse(childNodes.item(i), nodeTypes, traversedNodes);
            }
        }
        if (nodeTypes == null || nodeTypes.contains(root.getNodeType())) {
            traversedNodes.add(root);
        }
    }

    /**
     * Traverses the DOM Tree in post order
     * @param root the root of tree
     * @param nodeTypes set of nodes to be included in the traversed list.
     *                  Passing {@code null} will include all types of nodes
     * @return list of nodes visited along the post order traversal
     */
    public List<Node> postOrderTraverse(Node root, Set<Short> nodeTypes){
        List<Node> elements = new ArrayList<>();
        postOrderTraverse(root, nodeTypes, elements);
        return elements;
    }

    /**
     * Finds the leftmost lowest element in the tree
     * @param node the root of the tree
     * @return left most element in the tree rooted at given node
     */
    public Node getLowestLeftNode(Node node){
        Node lowestLeft = node;
        while (lowestLeft.hasChildNodes()) {
            lowestLeft = lowestLeft.getFirstChild();
        }
        return lowestLeft;
    }
}
