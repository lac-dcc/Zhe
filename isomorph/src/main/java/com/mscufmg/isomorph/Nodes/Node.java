package com.mscufmg.isomorph.nodes;

import java.util.ArrayList;
import java.io.Serializable;
import com.mscufmg.isomorph.nodes.LeafNode;
import com.mscufmg.isomorph.nodes.EmptyNode;

/**
 * This class is an abstraction to the tree nodes.
 */
public abstract class Node implements Serializable {
    protected String value;
    protected ArrayList<Node> children;
    private int begin;
    private int end;
    private Node parent;

    /**
     * Constructor.
     *
     * @param parent: the parent of this node.
     * @param value:  the value of this node.
     * @param begin:  the begining position of the tokens coverd by this node.
     * @param end:    the end position of the tokens coverd by this node.
     */
    public Node(Node parent, String value, int begin, int end) {
        this.parent = parent;
        this.value = value;
        this.begin = begin;
        this.end = end;
        this.children = new ArrayList<>();
    }

    /**
     * Get the value of this Node.
     *
     * @return the value of this Node.
     */
    public final String getValue() {
        return this.value;
    }

    /**
     * Get the begin of this Node.
     *
     * @return the begin of this Node.
     */
    public final int getBegin() {
        return this.begin;
    }

    /**
     * Get the end of this Node.
     *
     * @return the end of this Node.
     */
    public final int getEnd() {
        return this.end;
    }

    /**
     * Get the parent of this Node.
     *
     * @return the parent of this Node.
     */
    public final Node getParent() {
        return this.parent;
    }

    /**
     *  Get the ammount of children this node has.
     *
     *  @return the ammount of children this node has.
     */
    public abstract int getNumChildren();

    /**
     *  Add a new child to this node.
     *
     *  @param child: the child node to be added.
     */
    public abstract void addChild(Node node);

    /**
     *  Returns a children of this node.
     *
     *  @param i: the position of the child in relation to the node.
     *  @return the child of the node in the ith position, if node avaliable returns a EmptyNode.
     */
    public final Node getChild(int i) {
        return this.children.get(i);
    }
    
    /**
     * Check if two Nodes are equals.
     *
     * @param node: the node to check equality to.
     * @return      true if nodes are equals.
     */
    public final boolean equals(Node node) {
        return this.getClass().equals(node.getClass()) &&
               this.value.equals(node.getValue());
    }
    
    /**
     *  Check if two nodes are semantically equivalent.
     *
     *  @param value: a Node to check equivalence to.
     *  @return       true if two nodes are semantically equivalent.
     */
    public boolean matches(Node node){
        return this.equals(node);
    }
 
    /**
     *  Check if a node is Empty ( NullObject ).
     *
     *  @return true if a node is Empty.
     */
    public boolean isEmpty(){
        return false;
    }
    
    /**
     *  Check if a node is consider sensitive acording to this pattern.
     *
     *  @param node: a Node to check if it's value is sensitive.
     *  @return      return true if a value is consider sensitive by a pattern.
     */
    public boolean isSensitive(Node rule){ return false; }

    /**
     *  A especial case of checking sensitiviness, that checks agains a LeafNode.
     *
     *  @param node: a LeafNode to check if it's value is sensitive.
     *  @return      return true if a value is consider sensitive by a pattern.
     */
    protected boolean isSensitive(LeafNode node){ return false; }
      
    /**
     *  Merge two Nodes.
     *
     *  @param node: the node to be merged.
     *  @return a copy of the node param if the nodes match, return a HideNode otherwise.
     */
    public Node merge(Node node){
        if(node.equals(this))
            return node.copy();
        return new HideNode(null);
    }
    
    /**
     *  Merge this node with a empty node.
     *
     *  @param node: the empty node.
     *  @return a new EmptyNode.
     */
    protected Node merge(EmptyNode node){
        return new EmptyNode();
    }

    /**
     *  Copy a node.
     *
     *  @return a copy of this node.
     */
    public abstract Node copy();
    
    /**
     * Convert this Object to a String representatio.
     *
     * @return a String representation for this Node.
     */
    public String toString() {
        return this.value.toString();
    }
}
