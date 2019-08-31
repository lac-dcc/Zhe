package com.mscufmg.Zhe.trainer.nodes;

/**
 * This class represent a Root Node for a tree.
 */
public class RootNode extends InternalNode {
    /**
     * Constructor.
     */
    public RootNode() {
        super(null, "ROOT", -1, -1);
    }
    
    /**
     *  Copy a node.
     *
     *  @return a copy of this node.
     */
    public Node copy() {
        return new RootNode();
    }    
}
