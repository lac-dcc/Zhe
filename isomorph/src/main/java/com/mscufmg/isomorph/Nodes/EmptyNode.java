package com.mscufmg.isomorph.nodes;

import com.mscufmg.isomorph.nodes.LeafNode;
import com.mscufmg.isomorph.nodes.Node;

/**
 *  This class represents a Node that has no value ( is a implementation of NullObject ).
 */
public class EmptyNode extends Node {

    /**
     * Constructor.
     */
    public EmptyNode(){
        super(null, "EMPTY", -1, -1);
    }
    
    /**
     *  Check if two nodes are semantically equivalent.
     *
     *  @param value: a Node to check equivalence to.
     *  @return       true if two nodes are semantically equivalent.
     */
    @Override
    public boolean matches(Node value){
        return true;
    }

    /**
     *  Check if a node is Empty ( NullObject ).
     *
     *  @return true if a node is Empty.
     */
    @Override
    public boolean isEmpty(){
        return true;
    }
    
    /**
     *  Check if a node is consider sensitive acording to this pattern.
     *
     *  @param node: a LeafNode to check if it's value is sensitive.
     *  @return      return true if a value is consider sensitive by a pattern.
     */
    @Override
    protected boolean isSensitive(LeafNode node){
        return true;
    }
    
    /**
     *  Get the ammount of children this node has.
     *
     *  @return the ammount of children this node has.
     */
    @Override
    public int getNumChildren(){return 0;}

    /**
     *  Add a new child to this node.
     *
     *  @param child: the child node to be added.
     */
    @Override
    public void addChild(Node node){}

    /**
     *  Copy a node.
     *
     *  @return a copy of this node.
     */
    public Node copy(){
        return new EmptyNode();
    }

    /**
     *  Merge two Nodes.
     *
     *  @param node: the node to be merged.
     *  @return      a new EmptyNode().
     */
    public Node merge(Node node){
        return new EmptyNode();
    }
}
