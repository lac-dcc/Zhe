package com.mscufmg.benchmarks;

import java.util.logging.Logger;
/**
 * Hello world!
 *
 */
public class HelloWorld 
{
    private static Logger LOGGER = Logger.getLogger(HelloWorld.class.getName());

    public static void main( String[] args )
    {
        double max = 400000.0;

        if (args.length > 0)
            max = Integer.parseInt(args[0]);
        for(double i = 0; i < max; i++){
            LOGGER.warning("Hello World");
        }
    }
}
