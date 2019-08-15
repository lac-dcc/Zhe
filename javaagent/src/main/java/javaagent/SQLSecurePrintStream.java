package com.mscufmg.javaagent;

import com.mscufmg.isomorph.SQLTree;
import com.mscufmg.isomorph.nodes.LeafNode;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;

import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.JSQLParserException;

/**
 * This class implements a logic to create a Secure Print Stream from SQL Queries.
 */
public class SQLSecurePrintStream extends PrintStream {
    private SQLTree pattern;
    private OutputStream outStream;

    /**
     *  Contructor.
     *
     *  @param outStream: an Output Stream.
     *  @param filename:  the filename containing the SQLTree Obfuscation pattern.
     */
    public SQLSecurePrintStream(OutputStream outStream, String filename) throws IOException, ClassNotFoundException{
        super(outStream, true);
        this.outStream = outStream;
        this.pattern = SQLTree.deserialize(filename);
    }
    
    /**
     *  Parse a SQL Query.
     *
     *  @param s: the SQL Query to parse.
     */
    private SimpleNode parse(String s) throws JSQLParserException {
        return (SimpleNode) CCJSqlParserUtil.parseAST(s);
    }
    
    /**
     *  Output a String to the Output Stream.
     *
     *  @param s: the String to output.
     */
    public void print(String s){
        SimpleNode SQLNode = null;
        try {
            SQLNode = this.parse(s);
        } catch (JSQLParserException e) {}
        
        String output = s;

        if(SQLNode != null){
            output = this.formatSQL(new SQLTree(SQLNode), s);
        }

        try {
            this.out.write(output.getBytes());
        } catch (IOException e){}

    }

    /**
     *  Output a character to the Output Stream.
     *
     *  @param c: the character to output.
     */
    public void print(char c){
        try{
            this.out.write(c);
        } catch(IOException e) {}
    }
    
    /**
     *  Output a String to the Output Stream with a new line character on the end.
     *
     *  @param s: the String to output.
     */
    public void println(String s){
        this.print(s);
        this.print('\n');
    }
    
    /**
     *  Redacts a sensitive literal in a SQL Query.
     *
     *  @param sql:  the SQL Query to be redacted.
     *  @param node: the LeafNode contaning the sensitive literal.
     */
    private String redact(String sql, LeafNode node){
        String resp = sql.substring(0, node.getBegin()-1);
        for(int i = node.getBegin(); i <= node.getEnd(); i++)
            resp += "*";
        resp += sql.substring(node.getEnd());
        return resp;
    }
    
    /**
     *  Rewrite a SQL Query hiding all it's literals.
     *  
     *  @param tree: the query SQLTree.
     *  @param sql:  the String containing the query.
     */
    private String formatSQL(SQLTree tree, String sql){
        String resp = sql;
        if(this.pattern.matches(tree)){
            for(LeafNode node : pattern.getSensitiveLiterals(tree)){
                resp = this.redact(resp, node);
            }
        }   
        return resp;
    }
}
