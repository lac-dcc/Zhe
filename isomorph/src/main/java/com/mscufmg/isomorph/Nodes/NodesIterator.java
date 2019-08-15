package com.mscufmg.isomorph.nodes;

import com.mscufmg.isomorph.nodes.Node;

/**
 *  This class represents a DFS to iterate over two SQLTrees simultaneously. 
 */ 
public class NodesIterator{
    public final Node v1;
    public final Node v2;
    private int current;
    private int depth;
    private final int numChildren;
    private NodesIterator parent;

    /**
     * Constructor.
     *
     * @param v1:     a Node from the first tree.
     * @param v2:     a Node from the second tree.
     * @param parent: the parent of both nodes.
     * @param depth:  the current tree depth.
     */
    public NodesIterator(Node v1, Node v2, NodesIterator parent, int depth) {
        this.v1 = v1;
        this.v2 = v2;
        this.numChildren = Math.max(this.v1.getNumChildren(), this.v2.getNumChildren());
        this.current = 0;
        this.parent = parent;
        this.depth = depth;
    }

    /**
     * Constructor.
     *
     * @param v1: a Node from the first tree.
     * @param v2: a Node from the second tree.
     */
    public NodesIterator(Node v1, Node v2) {
        this(v1, v2, null, 0);
    }

    /**
     * Helper method to manage the next child from the current node.
     *
     * @param i:    number of the child being getter.
     * @param node: the node to get the child from.
     */
    private Node getNextNode(int i, Node node){
        if(i < node.getNumChildren())
            return node.getChild(i);
        return (Node) new EmptyNode();
    }

    /**
     * Check if has more nodes to iterate over.
     *
     * @return true if has more nodes to iterate.
     */
    public boolean hasNext() {
        return this.current < this.numChildren || (parent != null && parent.hasNext());
    }

    /**
     * Returns the next pair of nodes.
     *
     * @return the next pair of nodes from a DFS strategy.
     */
    public NodesIterator getNext() {
        if(this.current < this.numChildren){
            NodesIterator next = new NodesIterator(this.getNextNode(this.current, this.v1), 
                                                   this.getNextNode(this.current, this.v2),
                                                   this, 
                                                   this.depth + 1);
            this.current += 1; 
            return next;
        } else if(this.parent != null) {
            return this.parent.getNext();    
        }
        return null;
    }

    /**
     * Convert this to a String representation.
     *
     * @return a String representation for this object.
     */
    public String toString() {
        String resp = "";
        for(int i = 0; i < this.depth; i++)
            resp += " ";
        resp += this.v1 + " | " + this.v2;
        return resp;
    }
}
