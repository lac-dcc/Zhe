package com.mscufmg.Zhe.trainer.nodes;


/**
 *  This class represents a Internal Node inside the SQLTree.
 */
public class InternalNode extends Node {
    /**
     * Constructor.
     *
     * @param parent: the parent of this node.
     * @param value:  the value of this node.
     * @param begin:  the begining position of the tokens coverd by this node.
     * @param end:    the end position of the tokens coverd by this node.
     */
    public InternalNode(Node parent, String value, int begin, int end){
        super(parent, value, begin, end);
    }

    /**
     *  Add a new child to this node.
     *
     *  @param child: the child node to be added.
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     *  Get the ammount of children this node has.
     *
     *  @return the ammount of children this node has.
     */
    @Override
    public int getNumChildren(){
        return this.children.size();
    }

    
    /**
     *  Copy a node.
     *
     *  @return a copy of this node.
     */
    public Node copy(){
        return new InternalNode(this.getParent(), this.getValue(), this.getBegin(), this.getEnd());
    }
    
    /**
     * Convert this Object to a String representatio.
     *
     * @return a String representation for this Node.
     */
    @Override
    public String toString() {
        return "NODE "+ super.toString();
    }
}
