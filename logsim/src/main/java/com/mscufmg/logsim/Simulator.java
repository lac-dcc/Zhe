package com.mscufmg.logsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import com.mscufmg.logsim.distributions.Distribution;
import com.mscufmg.logsim.util.Timer;
import com.mscufmg.logsim.util.Config;

/**
 * Log Simulator
 *
 */
public class Simulator
{
    private Timer timer;
    private Random generator;
    private Distribution dist;

    public Simulator(Distribution dist)
    {
        this.dist = dist;
        this.timer = new Timer("yyyy/MM/dd HH:mm:ss");
        this.generator = new Random(Config.SEED);
    }

    private String choose(File f) throws FileNotFoundException
    {
        String result = null;
        int n = 0;
        for(Scanner sc = new Scanner(f); sc.hasNext(); )
        {
            ++n;
            String line = sc.nextLine();
            if(this.generator.nextInt(n) == 0)
                result = line;
        }

        return result;
    }

    public void run(String queriesFile, double maxTime, double numEvents, double timeMult) throws java.lang.InterruptedException, FileNotFoundException {
        double step = maxTime/numEvents;

        for(double i = 0.0; i < maxTime; i += step){
            double runEventProb = this.generator.nextDouble();
            double curProb = this.dist.pdf(i);

            if( runEventProb < curProb){
                double prob = (i/maxTime) * 100.0;
                System.out.print( String.format("%.1f", prob)+"% - ");
                System.out.println(this.choose(new File(queriesFile)));
            }else{
                Thread.sleep((long)(step * timeMult));
            }

        }
    }


}  
