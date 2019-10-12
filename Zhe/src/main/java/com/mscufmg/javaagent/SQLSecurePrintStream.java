package com.mscufmg.Zhe.javaagent; 

import org.antlr.v4.runtime.RuleContext;

import com.mscufmg.Zhe.trainer.ParserFacade;
import com.mscufmg.Zhe.trainer.SQLTree;
import com.mscufmg.Zhe.trainer.nodes.LeafNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.JSQLParserException;

/**
 * This class implements a logic to create a Secure Print Stream from SQL Queries.
 */
public class SQLSecurePrintStream extends CustomPrintStream {

    private SQLTree pattern;
    private String grammarRoot; 
    private ParserFacade facade;
    private long numStringEvents;
    private long numSQLQueries;
    private final String key;
    private boolean decrypt;
    private final ArrayList<String> START_SQL = new ArrayList(Arrays.asList("select", "create", "update", "delete", "use", "set", "show", "SELECT", "CREATE", "UPDATE", "DELETE", "USE", "SET", "SHOW"));

    /**
     *  Contructor.
     *
     *  @param outStream: an Output Stream.
     *  @param filename:  the filename containing the SQLTree Obfuscation pattern.
     */
    public SQLSecurePrintStream(OutputStream outStream, String filename, String key, boolean decrypt) throws IOException, ClassNotFoundException{
        super(outStream, true);
        this.facade = new ParserFacade(this.grammarRoot);
        this.pattern = SQLTree.deserialize(filename);
        this.key = key;
        this.decrypt = decrypt;
        this.grammarRoot = "/Users/joaosaffran/Research/Zhe/grammar";

    }
    public SQLSecurePrintStream(OutputStream outStream, String filename, String key) throws IOException, ClassNotFoundException{
        this(outStream, filename, key, false);
    }
    /**
     *  Parse a SQL Query.
     *
     *  @param s: the SQL Query to parse.
     */
    private SimpleNode jSQLParse(String s) throws JSQLParserException {
        return (SimpleNode) CCJSqlParserUtil.parseAST(s);
    }

    private RuleContext antlrParse(String s) throws Exception {
        return this.facade.parse(s);
    }

    public void write (byte[] buffer, int offset, int len)
    {

        if(buffer[len -1] != '\n'){
            String output = this.processBytes(Arrays.copyOfRange(buffer, offset, offset + len));

            buffer = output.getBytes();
            len = output.length(); 
        }

        try
        {

            out.write(buffer, offset,len);

            if (auto_flush)
                flush ();
        }
        catch (InterruptedIOException iioe)
        {
            Thread.currentThread ().interrupt ();
        }
        catch (IOException e)
        {
            setError ();
        }
    }

    public void write (int oneByte)
    {
        try
        {
            out.write (oneByte & 0xff);

            if (auto_flush && (oneByte == '\n'))
                flush ();
        }
        catch (InterruptedIOException iioe)
        {
            Thread.currentThread ().interrupt ();
        }
        catch (IOException e)
        {
            setError ();
        }
    }

    protected void writeChars(char[] buf, int offset, int count)
            throws IOException
        {
            byte[] bytes = (new String(buf, offset, count)).getBytes();
            this.write(bytes, 0, bytes.length);
        }

    protected void writeChars(String str, int offset, int count)
            throws IOException
        {
            byte[] bytes = str.substring(offset, offset+count).getBytes();
            this.write(bytes, 0, bytes.length);
        }

    public String processBytes(byte[] bytes){
        
        String s = new String(bytes);

        this.numStringEvents += 1;
        String sql = getSQLQuery(s.replaceAll("\\s", " ").split(" "));

        RuleContext SQLNode = null;
        try {
            SQLNode = this.antlrParse(sql);
        } catch (Exception e) {}

        String output = s;

        if(SQLNode != null){
            this.numSQLQueries += 1;
            String newSQL = this.formatSQL(new SQLTree(SQLNode, this.facade.getRulesNames()), sql);
            output = s.replace(sql, newSQL);
        } 
        return output;
    }

    /**
     *  Redacts a sensitive literal in a SQL Query.
     *
     *  @param sql:  the SQL Query to be redacted.
     *  @param node: the LeafNode contaning the sensitive literal.
     */
    private String redact(String sql, ArrayList<LeafNode> nodes){
        String resp = sql.substring(0, nodes.get(0).getBegin() - 1);

        for(int i = 0; i < nodes.size(); i++){
            LeafNode node = nodes.get(i);
            String text = sql.substring(node.getBegin() -1, node.getEnd());
            String encriptText; 
            if (this.decrypt)
                encriptText = AES.decrypt(text, this.key); 
            else
                encriptText = AES.encrypt(text, this.key); 
            resp += encriptText;

            if(i + 1 < nodes.size())
                resp += sql.substring(node.getEnd(), nodes.get(i+1).getBegin()-1);
            else
                resp += sql.substring(node.getEnd());
        }
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
            resp = this.redact(resp, pattern.getSensitiveLiterals(tree));
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
            this.antlrParse(sql); 
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
