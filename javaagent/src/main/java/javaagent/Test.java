package com.mscufmg.javaagent;
import java.util.Scanner;

public class Test{
    public static void main(String[] args){
        System.out.println("Input some text to see what is going to be hidden.");
        Scanner input = new Scanner(System.in);
        
        System.out.print(">> ");
        String text = input.nextLine();
        
        while(text.length() > 0){
            System.out.print("== ");
            System.out.println(text);
            System.out.print(">> ");
            text = input.nextLine();
        }
    }
}
