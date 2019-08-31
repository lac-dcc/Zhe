package com.mscufmg.Zhe.logsim.distributions;

public class Normal extends Distribution {
    private double mu;
    private double sigma;

    public Normal(double mu, double sigma){
        super();
        this.mu = mu;
        this.sigma = sigma;
    }

    @Override
    public double pdf(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    @Override
    protected double cdf(int x){
        double z = (x - this.mu) / this.sigma;
        if (z < -8) return 0.0;
        if (z >  8) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * pdf(z);
    }    
    
    @Override
    public String toString(){
        return "Normal(mu=" + this.mu + ", sigma="+ this.sigma + ")";
    } 
}
