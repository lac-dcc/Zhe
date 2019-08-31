package com.mscufmg.Zhe.trainer.nodes;

public class LeafNode extends Node {
    
    /**
     * Constructor.
     *
     * @param parent: the parent of this node.
     * @param value:  the value of this node.
     * @param begin:  the begining position of the tokens coverd by this node.
     * @param end:    the end position of the tokens coverd by this node.
     */
    public LeafNode(Node parent, String value, int begin, int end) {
        super(parent, value, begin, end);
    }

    /**
     * Check if this node is consider Sensitive by a pattern.
     *
     * @param rule: A Node indicating a sensitivive literal rule.
     * @return      true if this node is consider sensitive.
     */
    @Override
    public boolean isSensitive(Node rule){
        return rule.isSensitive(this);
    }
    
    /**
     * Check if a node is consider Sensitive by this pattern.
     *
     * @param node: A LeafNode indicating a sensitivive literal.
     * @return      true if this node is equals to the node param.
     */
    @Override
    protected boolean isSensitive(LeafNode node){
        return !node.equals(this);
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
        return new LeafNode(this.getParent(), this.getValue(), this.getBegin(), this.getEnd());
    }
    
    /**
     * Convert this Object to a String representatio.
     *
     * @return a String representation for this Node.
     */
    @Override
    public String toString(){
        return "LEAF " + super.toString();
    }
}
