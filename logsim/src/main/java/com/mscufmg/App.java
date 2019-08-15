package com.mscufmg;

import com.mscufmg.logsim.distributions.*;
import com.mscufmg.logsim.Simulator;

 /**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws java.lang.InterruptedException, java.io.FileNotFoundException 
    {
        Simulator s = new Simulator(new Normal(1, 0.3));
        // Simulator s = new Simulator(new Exponential(1));
        s.run("../analysis/queries_select.txt", 2.0, 100000.0, 100000.0);
    }
}  
