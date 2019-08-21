package com.mscufmg.javaagent;

import com.mscufmg.isomorph.SQLTree;
import com.mscufmg.isomorph.nodes.LeafNode;

import java.util.ArrayList;
import java.util.Arrays;
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
    private long numStringEvents;
    private long numSQLQueries;
    private final ArrayList<String> START_SQL = new ArrayList(Arrays.asList("select", "create", "update", "delete", "use", "set", "show"));

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
        this.numStringEvents += 1;
        String sql = getSQLQuery(s.split(" "));
        
        SimpleNode SQLNode = null;
        try {
            SQLNode = this.parse(sql);
        } catch (JSQLParserException e) {}
        
        String output = s;

        if(SQLNode != null){
            this.numSQLQueries += 1;
            String newSQL = this.formatSQL(new SQLTree(SQLNode), sql);
            output = s.replace(sql, newSQL);
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

    private ArrayList<String> combine(ArrayList<String> l1, ArrayList<String>l2){
        ArrayList<String> resp = new ArrayList();
        resp.addAll(l1);
        resp.addAll(l2);
        return resp;
    }

    private String join(String sep, ArrayList<String> list){
        if(list.size() <= 0)
            return "";
        String resp = list.get(0);
        for(int i = 1; i < list.size(); i++)
            resp += sep + list.get(i);
        return resp;

    }

    private String getSQLQuery(String[] tokens){
        ArrayList<String> prefix = new ArrayList();
        ArrayList<String> sql = new ArrayList();
        ArrayList<String> suffix = new ArrayList();
        
        int state = 0;

        for(String token: tokens){

            if(state == 0) {
                
                if(START_SQL.contains(token)){
                    state = 1;
                    sql.add(token);
                } else{
                    prefix.add(token);
                }
                 
            } else if(state == 1) {
                
                if(isValidSQL(this.join(" ", sql))){
                    state = 2;
                    suffix.add(token);
                }else{ 
                    sql.add(token);
                }

            } else if(state == 2) {
                suffix.add(token);

                ArrayList<String> combination = this.combine(sql, suffix);
                if(isValidSQL(this.join(" ", combination))){
                    sql = combination;
                    suffix.clear();
                }
            }
        }

        ArrayList<String> combination = this.combine(sql, suffix);
        if(suffix.size() > 0 && isValidSQL(this.join(" ", combination))){
            sql = combination;
        }
        return this.join(" ", sql);
    }

    private boolean isValidSQL(String sql){
        try{
            this.parse(sql); 
        } catch(Exception e){
            return false;
        }
        return true;
    }

    public long getNumStringEvents(){
        return this.numStringEvents;
    }
    
    public long getNumSQLQueries(){
        return this.numSQLQueries;
    }
}
