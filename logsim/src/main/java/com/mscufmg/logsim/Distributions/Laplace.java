package com.mscufmg.logsim.distributions;

public class Laplace extends Distribution {
    private double mu;
    private double b;

    public Laplace(double mu, double b){
        super();
        this.mu = mu;
        this.b = b;
    }
    
    @Override
    public double pdf(double x){
        return (1.0/(2.0 * this.b)) * Math.exp(-(Math.abs(x - this.mu)/this.b));
    }

    @Override
    protected double cdf(int x){
        double normalization = (x - this.mu)/b;

        if(x <= this.mu){
            return Math.exp(normalization)/2.0;
        } 
        return 1.0 - Math.exp(-normalization)/2.0;
    }    
}
