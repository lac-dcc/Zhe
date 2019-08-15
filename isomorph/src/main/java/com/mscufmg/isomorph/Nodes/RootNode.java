package com.mscufmg.isomorph.nodes;

import com.mscufmg.isomorph.nodes.InternalNode;
import com.mscufmg.isomorph.nodes.HideNode;

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
