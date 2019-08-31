package com.mscufmg.Zhe.logsim.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mscufmg.Zhe.logsim.distributions.Distribution;
import com.mscufmg.Zhe.logsim.distributions.Exponential;

@Parameters(commandDescription = "Command to configure a new Normal Distribution")
public class ExponentialCommand {
    
    @Parameter(names={"--Lambda"}, description="Exponential lambda parameter")
    private Double lambda = 1.0;

    public Distribution getDistribution(){
        return new Exponential(this.lambda);
    }
}
