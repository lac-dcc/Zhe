package com.mscufmg.javaagent;
import java.util.Scanner;

public class Test{
    public static void main(String[] args){
        System.out.println("Input some text to see what is going to be hidden.");
        Scanner input = new Scanner(System.in);
        String out = AES.encrypt("banana 1234567890 ROUXINOL", "Senha");
        System.out.println(out);
        System.out.println(AES.decrypt(out, "Senha"));
        System.out.print(">> ");
        String text = input.nextLine();
        
        while(text.length() > 0){
            System.out.println("== " + text);
            System.out.print(">> ");
            text = input.nextLine();
        }
    }
}
