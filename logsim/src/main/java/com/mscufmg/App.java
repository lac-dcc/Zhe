package com.mscufmg;

import com.mscufmg.logsim.distributions.*;
import com.mscufmg.logsim.Simulator;
import com.mscufmg.javaagent.SQLSecurePrintStream;

 /**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws java.lang.InterruptedException, java.io.FileNotFoundException 
    {
        // Distribution d = new Normal(1, 0.3); 
        Distribution d = new Exponential(1); 
        Simulator s = new Simulator(d);
        s.run(args[0], 2.0, 100000.0, 100000.0);
        if(System.out instanceof SQLSecurePrintStream){

            System.err.print(((SQLSecurePrintStream)System.out).getNumStringEvents() + ", "+ ((SQLSecurePrintStream)System.out).getNumSQLQueries() + ", \""+d.toString() + "\"");
        } else {
            System.err.print("0, 0, \""+ d.toString() + "\"");
        }
    }   
}  
