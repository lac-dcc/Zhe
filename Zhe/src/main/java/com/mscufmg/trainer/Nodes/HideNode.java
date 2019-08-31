package com.mscufmg.Zhe.trainer.nodes;


/**
 *  This class represents a Node that indicate if another Node is should obligatory being hidden.
 */
public class HideNode extends LeafNode {

    /**
     *  Constructor.
     *
     *  @param parent: the parent of this HideNode.
     */
    public HideNode(Node parent) {
        super(parent, "HIDE", -1, -1);
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
     *  Check if two nodes are semantically equivalent.
     *
     *  @param value: a Node to check equivalence to.
     *  @return       true if two nodes are semantically equivalent.
     */
    @Override
    public boolean matches(Node node){
        return true;
    }

    /**
     *  Copy a node.
     *
     *  @param node: the node to be merged.
     *  @return a copy of this node.
     */
    public Node copy(){
        return new HideNode(this.getParent());
    }
    
    /**
     *  Merge two Nodes.
     *
     *  @return a new HideNode().
     */
    public Node merge(Node node){
        return new HideNode(null);
    }
}
