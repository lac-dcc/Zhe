package com.mscufmg.Zhe.trainer;

import com.mscufmg.Zhe.trainer.SQLTree;
import com.mscufmg.Zhe.trainer.nodes.LeafNode;
import com.beust.jcommander.converters.IParameterSplitter;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

class SemiColonSplitter implements IParameterSplitter {
    public List<String> split(String value) {
      return Arrays.asList(value.split(";"));
    }
}


/**
 * This class implements a terminal application to help using the Obfuscator.
 */
@Parameters(commandDescription = "Create a obfuscation pattern given some examples")
public class TrainerCommand
{
    @Parameter(names={"-q", "--queries"}, description="Input example Queries", required=true, variableArity=true, splitter=SemiColonSplitter.class)
    private List<String> queries;

    @Parameter(names={"-t", "--test"}, description="Test Obfuscaation Pattern on given Queries")
    private boolean test = false;

    @Parameter(names={"-f", "--filename"}, description="Obfuscation Pattern filename")
    private String filename = "Tree.ser";

    private SimpleNode parse(String s) {
        try{
            return (SimpleNode) CCJSqlParserUtil.parseAST(s);
        } catch(JSQLParserException e){
            System.out.println("Error on parsing SQL Query: Is this SQL Valid?\n" + "SQL: " + s);
            return null;
        } 
    }

    private String redact(String sql, LeafNode node){
        String resp = sql.substring(0, node.getBegin()-1);
        for(int i = node.getBegin(); i <= node.getEnd(); i++)
            resp += "*";
        resp += sql.substring(node.getEnd());
        return resp;
    }

    private void test(String obfsPattern, List<String> queries){
        SQLTree tree;
        try{
            tree = SQLTree.deserialize(obfsPattern);
        } catch (IOException e){
            System.out.println("Error when deserialize pattern: File not found!");
            return;
        } catch(ClassNotFoundException e){
            System.out.println("Error when deserialize pattern: Class not found!");
            return;
        }
        for(String query : queries){
            SQLTree sql = new SQLTree(parse(query));
            if(tree.matches(sql)) {
                String newSQL = query; 

                for(LeafNode node : tree.getSensitiveLiterals(sql))
                    newSQL = redact(newSQL, node);

                System.out.println("+ " + query + "\n" + "= "+ newSQL + "\n");
            } else {
                System.out.println("- " + query);
            }
        }
    }

    private void train(List<String> queries, String outFile){
        SQLTree similarity = new SQLTree(parse(queries.get(0)));

        for(int i = 1; i< queries.size(); i++){
            SQLTree tree = new SQLTree(parse(queries.get(i)));
            similarity = similarity.merge(tree);
        }
        System.out.println(similarity);
        try{
            similarity.serialize(outFile);
        } catch (IOException e){
            System.out.println("Error when saving pattern: File already exist!");
            return;
        }
    }

    public void run(){
        if(this.test){
            this.test(this.filename, this.queries);
        }else{
            this.train(this.queries, this.filename);
        }
    }
}
