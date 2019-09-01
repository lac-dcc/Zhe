package com.mscufmg.Zhe.logsim;

import com.mscufmg.Zhe.trainer.SQLTree;
import com.mscufmg.Zhe.trainer.nodes.LeafNode;
import com.mscufmg.Zhe.logsim.distributions.Distribution;
import com.mscufmg.Zhe.javaagent.SQLSecurePrintStream;

import java.io.IOException;

import java.util.List;

import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


/**
 * This class implements a terminal application to help using the Obfuscator.
 */
@Parameters(commandDescription = "Simulate the Agent running over a logfile")
public class LogsimCommand
{
    @Parameter(names={"-l", "--logfile"}, description="Simulation logfile", required=true)
    private String logfile;
 
    @Parameter(names={"-d", "--duration"}, description="Maximum time value for the simulation")
    private Double totalDuration = 2.0;
    
    @Parameter(names={"-n", "--num-samples"}, description="Number of samples over the distribution")
    private Double numSamples = 100000.0;
    
    @Parameter(names={"-s", "--sleep"}, description="Sleep time for an event")
    private Double sleep = 1000000.0;

    public void run(Distribution d){
        Simulator s = new Simulator(d);
        try{
            s.run(this.logfile, this.totalDuration, this.numSamples, this.sleep);
        } catch(java.lang.InterruptedException e){
            System.out.println(e);
            return;
        } catch(java.io.FileNotFoundException e){
            System.out.println("Simulation error: Pattern file not found!");
            return;
        }
        if(System.out instanceof SQLSecurePrintStream){

            System.err.print(((SQLSecurePrintStream)System.out).getNumStringEvents() + ", "+ ((SQLSecurePrintStream)System.out).getNumSQLQueries() + ", \""+d.toString() + "\"");
        } else {
            System.err.print("0, 0, \""+ d.toString() + "\"");
        }
    }
}
