package com.mscufmg.isomorph;

import com.mscufmg.isomorph.SQLTree;
import com.mscufmg.isomorph.nodes.LeafNode;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.*;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;

/**
 * This class implements a terminal application to help using the Obfuscator.
 */
public class App 
{
    static SimpleNode parse(String s) throws JSQLParserException {
        return (SimpleNode) CCJSqlParserUtil.parseAST(s);
    }

    static String redact(String sql, LeafNode node){
        String resp = sql.substring(0, node.getBegin()-1);
        for(int i = node.getBegin(); i <= node.getEnd(); i++)
            resp += "*";
        resp += sql.substring(node.getEnd());
        return resp;
    }

    public static void main( String[] args ) throws JSQLParserException, IOException, ClassNotFoundException{
        Options options = new Options();

        Option help = new Option("help", "Print this message");
        options.addOption(help);

        Option redact = OptionBuilder.withArgName("treefile")
            .withDescription("Serialized tree path")
            .hasArg()
            .create("f");

        Option input = OptionBuilder.withArgName("queries")
            .withDescription("Input queries")
            .isRequired(true) 
            .hasArgs(Option.UNLIMITED_VALUES)
            .create("q");
        options.addOption(input);
        options.addOption(redact);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException exp) {
            formatter.printHelp( "Obfuscator", options);
            System.exit(1);
        }

        String[] queries = cmd.getOptionValues("q");

        if( cmd.hasOption( "help" ) ) {
            formatter.printHelp( "Obfuscator", options);
        } else if(cmd.hasOption("f")){ 
            SQLTree tree = SQLTree.deserialize(cmd.getOptionValue("f"));

            for(int i = 0; i < queries.length; i++){
                SQLTree sql = new SQLTree(parse(queries[i]));
                if(tree.matches(sql)) {
                    String newSQL = queries[i]; 
                    
                    for(LeafNode node : tree.getSensitiveLiterals(sql))
                        newSQL = redact(newSQL, node);
                    
                    System.out.println("+ " + queries[i] + "\n" + "= "+ newSQL + "\n");
                } else {
                    System.out.println("- " + queries[i]);
                }
            }
        } else {
            SQLTree similarity = new SQLTree(parse(queries[0]));

            for(int i = 1; i< queries.length; i++){
                SQLTree tree = new SQLTree(parse(queries[i]));
                similarity = similarity.merge(tree);
            }
            System.out.println(similarity);
            similarity.serialize("tree.ser");
        }
    }
}
