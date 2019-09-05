package com.mscufmg.Zhe.reader;

import com.mscufmg.Zhe.javaagent.SQLSecurePrintStream;

import java.io.IOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


/**
 * This class implements a terminal application to help using the Obfuscator.
 */
@Parameters(commandDescription = "Simulate the Agent running over a logfile")
public class ReaderCommand
{
    @Parameter(names={"-k", "--key"}, description="Key used to encrypt the file", required=true)
    private String key;
    
    @Parameter(names={"-f", "--filename"}, description="Logfile to decrypt", required=true)
    private String filename;
    
    @Parameter(names={"-p", "--pattern"}, description="Pattern used to find what to decrypt", required=true)
    private String pattern;
    

    public void run() throws java.lang.InterruptedException, FileNotFoundException, java.io.IOException, java.lang.ClassNotFoundException {
        System.setOut(new SQLSecurePrintStream(System.out, pattern, key, true));
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
        }
    }

}
