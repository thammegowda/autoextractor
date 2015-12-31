package edu.usc.cs.autoext.tree;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TreeNode is a wrapper for {@link Node} which includes additional functions for
 * efficiently computing the edit distance
 */
public class TreeNode {

    protected String nodeName;
    protected Node innerNode;
    protected TreeNode parent;
    protected List<TreeNode> children;
    protected TreeNode leftMostDescendant;
    protected int index;

    private TreeNode(String nodeName ) {
        this.nodeName = nodeName;
        this.leftMostDescendant = findLeftMostDescendant();
    }

    /**
     *Creates a tree node object
     * @param innerNode the DOM API node
     * @param parent the parent node. For the root node, set to {@code null}
     */
    public TreeNode(Node innerNode, TreeNode parent)  {
        this.innerNode = innerNode;
        this.parent = parent;
        if (innerNode.hasChildNodes()) {
            children = new ArrayList<>();
            NodeList childNodes = innerNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node ithNode = childNodes.item(i);
                if (ithNode.getNodeType() != Node.ELEMENT_NODE){
                    //skip all other nodes
                    continue;
                }
                TreeNode child = new TreeNode(ithNode, this);
                children.add(child);
            }
        }
        this.leftMostDescendant = findLeftMostDescendant();
        this.nodeName = innerNode.getNodeName();
        this.postOrderIndex(new AtomicInteger(0));
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getIndex() {
        return index;
    }

    /**
     *
     * @return true if this node has children; returns false otherwise
     */
    public boolean hasChildNodes(){
        return children != null && !children.isEmpty();
    }

    /**
     * gets the inner DOM API node to which this node is a wrapper
     * @return inner node
     */
    public Node getInnerNode() {
        return innerNode;
    }

    /**
     * gets parent node
     * @return gets parent node ; may return null
     * especially for the root node which doesnt have a parent.
     */
    public TreeNode getParent() {
        return parent;
    }

    /**
     * gets all children
     * @return list of children if they are present or null if they are absent
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * gets lowest leftmost descendant node.
     * This is same as {@link #findLeftMostDescendant()} except one:
     * this method returns cached state variable instead of finding on request
     * @return lowest leftmost descendant node
     */
    public TreeNode getLeftMostDescendant() {
        return leftMostDescendant;
    }

    /**
     * finds the lowest left most descendant
     * @return lowest left most
     * @see #getLeftMostDescendant()
     */
    public TreeNode findLeftMostDescendant(){
        return hasChildNodes() ? children.get(0).getLeftMostDescendant() :  this;
    }

    /**
     * pretty prints the tree
     */
    public void prettyPrint() {
        prettyPrint("", true);
    }

    private void prettyPrint(String prefix, boolean isTail) {
        String name = String.format("[%d] %s desc:[%s]", index,
                innerNode.getNodeName(), leftMostDescendant.index);
        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        if (hasChildNodes()) {
            for (int i = 0; i < children.size() - 1; i++) {
                children.get(i).prettyPrint(prefix + (isTail ? "    " : "│   "), false);
            }
            children.get(children.size() - 1).prettyPrint(prefix + (isTail ? "    " : "│   "), true);
        }
    }

    /**
     * Traverses the tree in post order
     * @param traversedNodes List of nodes to which the new nodes are to be appended
     */
    private void postOrderTraverse(List<TreeNode> traversedNodes){
        if (hasChildNodes()){
            for (TreeNode child : children) {
                child.postOrderTraverse(traversedNodes);
            }
        }
        traversedNodes.add(this);
    }

    /**
     * Traverses the Tree in post order
     * @return list of nodes visited along the post order traversal
     */
    public List<TreeNode> postOrderTraverse(){
        List<TreeNode> elements = new ArrayList<>();
        postOrderTraverse(elements);
        return elements;
    }

    /**
     * Indexes the tree nodes in post order
     * @param startIndex the starting index
     */
    public void postOrderIndex(AtomicInteger startIndex){
        if (hasChildNodes()) {
            for (TreeNode child : children) {
                child.postOrderIndex(startIndex);
                startIndex.incrementAndGet();
            }
        }
        this.index = startIndex.get();
    }

    @Override
    public String toString() {
        return String.format("[%d]%s", index, nodeName);
    }

    /**
     * gets key roots of the tree rooted at this tree. Key root is one whose leftmost descendant
     * is different than its immediate parent
     * @param keyRootsBuffer buffer for updating the key roots
     */
    private void getKeyRoots(List<TreeNode> keyRootsBuffer){
        if (hasChildNodes()) {
            for (TreeNode child : children) {
                child.getKeyRoots(keyRootsBuffer);
            }
        }
        if (this.parent == null || //root node wont have parent
                this.parent.leftMostDescendant.index != this.leftMostDescendant.index) {
            //left descendant is not same as parent's left descendant => its a key mode
            keyRootsBuffer.add(this);
        }
    }

    /**
     * gets key roots of the tree rooted at this tree. Key root is one whose leftmost descendant
     * is different than its immediate parent
     * @return list of all key root nodes
     */
    public List<TreeNode> getKeyRoots() {
        List<TreeNode> keyRoots = new ArrayList<>();
        getKeyRoots(keyRoots);
        return keyRoots;
    }


    public static TreeNode createDummyNode(String name){
        return new TreeNode(name);
    }

    public static void main(String[] args) throws Exception {
        DOMParser parser = new DOMParser();

        String pathname = "src/test/resources/html/simple/3.html";
        parser.parse(new InputSource(new FileReader(pathname)));
        Document document = parser.getDocument();

        TreeNode node = new TreeNode(document, null);
        node.postOrderIndex(new AtomicInteger(1));
        node.prettyPrint();

        List<TreeNode> nodes = node.postOrderTraverse();
        System.out.println(nodes);

        System.out.println(node.getKeyRoots());
    }
}
