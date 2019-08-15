package com.mscufmg.logsim.distributions;
import com.mscufmg.logsim.util.Config;
import java.util.Random;

public abstract class Distribution {

    private Random generator;

    public Distribution(){
        this.generator = new Random(Config.SEED);
    }

    public abstract double pdf(double x);

    protected abstract double cdf(int x);

    public int sample(){
        double randomNum = this.generator.nextDouble();
        double acDist = 0.0;
        int x = 0;

        while(acDist < randomNum){
            x += 1;
            acDist = this.cdf(x);
        }
        
        return x;
    }

}

