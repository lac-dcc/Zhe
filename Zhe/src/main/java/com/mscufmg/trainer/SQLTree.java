package com.mscufmg.Zhe.trainer;

import com.mscufmg.Zhe.trainer.nodes.Node;
import com.mscufmg.Zhe.trainer.nodes.RootNode;
import com.mscufmg.Zhe.trainer.nodes.InternalNode;
import com.mscufmg.Zhe.trainer.nodes.LeafNode;
import com.mscufmg.Zhe.trainer.nodes.NodesIterator;

import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.parser.Token;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

/**
 *  This class represents an abstraction to the SQL AST;
 *  It can be the actual AST for a query or an pattern indicating what to redact.
 */
public class SQLTree implements Serializable {
    private static final long serialVersionUID = 1L; 
    private RootNode root;

    /**
     *  Constructor.
     */
    public SQLTree() {
        this.root = new RootNode();
    }

    /**
     *  Constructor.
     *
     *  @param node: tree root node.
     */
    public SQLTree(RootNode node) {
        this.root = node;
    }

    /**
     *  Constructor.
     *
     *  @param node: JSQLParserTree root node.
     */
    public SQLTree(SimpleNode node) {
        this();
        this.parseTreeFromSimpleNode(node, (InternalNode) this.root);
    }

    public SQLTree(RuleContext node, String[] rulesNames){
        this();
        this.parseTreeFromRuleContext(node, (InternalNode) this.root, rulesNames);
    }

    /**
     * Root node getter.
     *
     * @return the tree RootNode;
     */
    public RootNode getRoot(){
        return this.root;
    }

    /**
     *  Check if two SQLTree have the same pattern.
     *
     *  @param tree: a SQLTree encoding a SQL Query.
     *  @return      return true if have this tree has the same pattern as the tree param.
     */
    public Boolean matches(SQLTree value){
        NodesIterator iter = new NodesIterator(value.getRoot(), this.getRoot());
        boolean resp = true;
        do{
            resp = resp && iter.v2.matches(iter.v1);
            iter = iter.getNext();
        } while(iter != null);

        return resp;
    }

    /**
     *  Merge two SQLTrees into one.
     *
     *  @param value: a SQLTree encoding a SQL Query.
     *  @return      a new SQLTree representing the obfucation pattern to hide 
     *               sensitive literals of those queries.
     */
    public SQLTree merge(SQLTree value){
        RootNode newRoot = new RootNode();
        this.mergeNodes(this.getRoot().getChild(0), value.getRoot().getChild(0), newRoot);
        return new SQLTree(newRoot);
    }

    /**
     *  Auxiliar method to transverse two SQLTrees mergin it's nodes.
     *
     *  @param value:    a SQLTree node.
     *  @param pattern:  a SQLTree obfuscation pattern node.
     *  @param parent:   the parent Node of the current node to be merged.
     */
    private void mergeNodes(Node value, Node pattern, Node parent){
        Node newNode = value.merge(pattern);

        if(!(newNode.isEmpty()))
            parent.addChild(newNode);

        int numChildren = Math.min(value.getNumChildren(), pattern.getNumChildren());

        for(int i = 0; i < numChildren; i++){
            Node child = value.getChild(i);
            this.mergeNodes(child, pattern.getChild(i), newNode);
        }
    }

    /**
     *  Convert a SimpleNodeTree to a SQLTree
     *
     *  @param node:   a SimpleNode to be converted into SQLTree Node.
     *  @param parent: current parent of the current node.
     */
    private void parseTreeFromSimpleNode(SimpleNode node, Node parent) {
        int numChildren = node.jjtGetNumChildren();
        Token token = node.jjtGetFirstToken();
        Node newNode;

        if(numChildren == 0) {
            newNode = new LeafNode(parent, token.image, token.beginColumn, token.endColumn);
        } else {
            newNode = new InternalNode(parent, node.toString(), token.beginColumn, token.endColumn);
            for(int i = 0; i < numChildren; i++) {
                SimpleNode child = (SimpleNode)(node.jjtGetChild(i));
                this.parseTreeFromSimpleNode(child, newNode);
            }
        }    
        parent.addChild(newNode);
    }

    private void parseTreeFromRuleContext(RuleContext node, Node parent, String[] rulesName) {
        int numChildren = node.getChildCount();
        Node newNode = new InternalNode(parent, rulesName[node.getRuleIndex()], 
                    0, 
                    0);

        for(int i = 0; i < numChildren; i++) {
            ParseTree child = node.getChild(i);
            if (child instanceof RuleContext){
                this.parseTreeFromRuleContext((RuleContext)child, newNode, rulesName);
            }else if(child instanceof TerminalNodeImpl){
                this.parseTreeFromRuleContext((TerminalNodeImpl)child, newNode);
            }
        }
        parent.addChild(newNode);
    }

    private void parseTreeFromRuleContext(TerminalNodeImpl node, Node parent) {
        int numChildren = node.getChildCount();
        Node newNode;

        if(numChildren == 0) {
            newNode = new LeafNode(parent, node.toString(), 
                    node.getPayload().getStartIndex() + 1, 
                    node.getPayload().getStopIndex() + 1);
        } else {
            newNode = new InternalNode(parent, node.toString(), 
                   0, 
                    0);

            for(int i = 0; i < numChildren; i++) {
                ParseTree child = node.getChild(i);
                this.parseTreeFromRuleContext((TerminalNodeImpl)child, newNode);
            }
        }    
        parent.addChild(newNode);
    }

    /**
     *  Get a list of sensitive literral in a SQLTree
     *
     *  @param value: a SQLTree encoding a SQL Query.
     *  @return       a list of sensitive leafs of a SQL Query.
     */
    public ArrayList<LeafNode> getSensitiveLiterals(SQLTree value){
        ArrayList<LeafNode> sensitive = new ArrayList<>();
        NodesIterator iter = new NodesIterator(value.getRoot(), this.getRoot());        
        do{
            if(iter.v1.isSensitive(iter.v2))
                sensitive.add((LeafNode)iter.v1);
            iter = iter.getNext();
        } while(iter != null); 
        return sensitive;
    }

    /**
     *  Saves this SQLTree on a file.
     *  @param filename: the filename to save this object on.
     */
    public void serialize(String filename) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(file);

        out.writeObject(this);

        out.close();
        file.close();

        System.out.println("Object has been serialized");
    }

    /**
     *  Loads a serialize SQLTree
     *
     *  @param filename: the filename of a serialize SQLTree.
     *  @return          the SQLTree.
     */
    public static SQLTree deserialize(String filename) throws IOException, ClassNotFoundException {
        SQLTree resp = new SQLTree();
        FileInputStream file = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(file);

        resp = (SQLTree) in.readObject();

        in.close();
        file.close();
        System.out.println("Object has been deserialized");

        return resp;
    }

    /**
     *  Convert this SQLTree into String format.
     *
     *  @return a String representation to this SQLTree.
     */
    public String toString() {
        return this.toString(this.root, "");
    }

    /**
     *  Helper method to transverse this SQLTree and turnit into a String.
     *
     *  @param node:    the current node being transversed.
     *  @param prefix:  a prefix to be add to the node representation.
     *  @return         a String representation to a SQLTree.
     */
    private String toString(Node node, String prefix) {
        String resp = prefix + node.toString();
        for(int i = 0; i < node.getNumChildren(); i++){
            Node child = node.getChild(i);
            resp += "\n" + this.toString(child, prefix + " ");
        }
        return resp;
    }

}
