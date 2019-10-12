package com.mscufmg.Zhe;

import com.mscufmg.Zhe.trainer.ParserFacade;
import com.mscufmg.Zhe.trainer.SQLTree;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.mscufmg.Zhe.trainer.TrainerCommand;
import com.mscufmg.Zhe.logsim.LogsimCommand;
import com.mscufmg.Zhe.logsim.commands.NormalCommand;
import com.mscufmg.Zhe.logsim.commands.ExponentialCommand;
import com.mscufmg.Zhe.reader.ReaderCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.MissingCommandException;

import java.io.File;


public class Main {

    @Parameter(names={"-h", "--help"}, description="Help/Usage", help=true)
    private boolean help;

    private void processCommandLineArguments(String[] args){
        TrainerCommand tc = new TrainerCommand();
        LogsimCommand lc = new LogsimCommand();
        ReaderCommand rc = new ReaderCommand();

        final JCommander commander = JCommander.newBuilder()
            .programName("Zhe: Hide sensitive information on SQL logs")
            .addObject(this)
            .addCommand("train", tc)
            .addCommand("simulate", lc)
            .addCommand("read", rc)
            .build();

        NormalCommand nc = new NormalCommand();
        ExponentialCommand ec = new ExponentialCommand();
        JCommander simCommander = commander.getCommands().get("simulate");
        simCommander.addCommand("normal", nc);
        simCommander.addCommand("exponential", ec);

        try{
            commander.parse(args);
        } catch(MissingCommandException e){
            System.out.println("Wrong parameter!");
            commander.usage();
            return;
        }
        if(help)
            commander.usage();
        else{

            String cmd = commander.getParsedCommand();

            if(cmd == null){
                System.out.println("Command missing!");
                commander.usage();
            } else {
                switch(cmd){
                    case "train":
                        tc.run();
                        break;
                    case "simulate":
                        String distribution = simCommander.getParsedCommand();
                        if(distribution == null){
                            System.out.println("Distribution missing!");
                            commander.usage();
                        }else{
                            switch(distribution){
                                case "normal":
                                    lc.run(nc.getDistribution()); 
                                    break;
                                case "exponential":
                                    lc.run(ec.getDistribution()); 
                                    break;
                                default:
                                    System.out.println("Distribution not avaliable!");
                                    commander.usage();
                                    break;
                            }
                        }
                        break;
                    case "read":
                        try{
                            rc.run();
                        } catch(Exception e){
                            System.out.println("Erro when reading log: " + e);
                        }
                        break;
                }
            }
        }
    }
    /**
     * Main executable function to demonstrate JCommander-based command-line processing.
     *
     * @param arguments Command-line arguments.
     */

    public static void main(final String[] arguments)
    {
        new Main().processCommandLineArguments(arguments);
        // try{
        //     ParserFacade facade = new ParserFacade("/Users/joaosaffran/Research/Zhe/grammar");
        //     RuleContext ctx = facade.parse("Sbananan");
        //     System.out.println(new SQLTree(ctx, facade.getRulesNames()));
        // } catch (Exception e){
        //     e.printStackTrace();
        // }
    }
}
