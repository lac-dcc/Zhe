package javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;

import java.security.ProtectionDomain;

import java.io.IOException;
import com.mscufmg.javaagent.SQLSecurePrintStream;

public class Agent{ 
    /**
     * If the agent is attached to a JVM on the start,
     * this method is invoked before {@code main} method is called.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void premain(final String agentArgs,
                               final Instrumentation inst) throws IOException, ClassNotFoundException {

        System.setOut(new SQLSecurePrintStream(System.out,agentArgs));
    }
 
    /**
     * If the agent is attached to an already running JVM,
     * this method is invoked.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void agentmain(final String agentArgs,
                                 final Instrumentation inst) throws IOException, ClassNotFoundException {
        System.setOut(new SQLSecurePrintStream(System.out,agentArgs));
    }
}
