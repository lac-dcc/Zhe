package com.mscufmg.Zhe.logsim.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mscufmg.Zhe.logsim.distributions.Distribution;
import com.mscufmg.Zhe.logsim.distributions.Normal;

@Parameters(commandDescription = "Command to configure a new Normal Distribution")
public class NormalCommand {
    
    @Parameter(names={"--mu"}, description="Normal mu parameter")
    private Double mu = 1.0;

    @Parameter(names={"--sigma"}, description="Normal sigma parameter")
    private Double sigma = 0.3;

    public Distribution getDistribution(){
        return new Normal(this.mu, this.sigma);
    }
}
