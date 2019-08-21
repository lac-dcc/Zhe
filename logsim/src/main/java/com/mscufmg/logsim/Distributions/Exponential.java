package com.mscufmg.logsim.distributions;

public class Exponential extends Distribution {
    private double lambda;

    public Exponential(double lambda){
        super();
        this.lambda = lambda;
    }

    @Override
    public double pdf(double x){
        return this.lambda * Math.exp(-this.lambda * x);
    }

    @Override
    protected double cdf(int x){
        return 1.0 - Math.exp(-this.lambda * x);
    }    

    @Override
    public String toString(){
        return "Exponential(lambda="+this.lambda+")";
    }
}
