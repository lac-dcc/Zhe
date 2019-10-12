package com.mscufmg.Zhe.trainer;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.nio.charset.Charset;
import java.nio.file.Files;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.lang.ClassNotFoundException;
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

class GrammarLoader extends ClassLoader{

    private ClassLoader loader;

    public GrammarLoader(String rootDirPath) throws MalformedURLException{        
        File file = new File(rootDirPath);

        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        this.loader = new URLClassLoader(urls);
    }

    public Class loadClass(String className) throws ClassNotFoundException{
        return this.loader.loadClass(className);
    }
}

public class ParserFacade {
    private ClassLoader loader;
    private Class lexerClass;
    private Class parserClass;
    private Object parser; 

    public ParserFacade(String rootPath){
        try{
            loader = new GrammarLoader(rootPath);
        
            this.lexerClass = loader.loadClass("SqlLexer");
            this.parserClass = loader.loadClass("SqlParser");
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
    }

    private String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    public String[] getRulesNames(){
        return this.getRulesNames(this.parser);
    }

    public String[] getRulesNames(Object parser){
        try{
            Method method = parserClass.getMethod("getRuleNames");
            return (String[])method.invoke(parser);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    public void explore(RuleContext ctx){
        this.explore(ctx, 0, this.getRulesNames(this.parser));
    }
    
    private void explore(RuleContext ctx, int indentation, String[] rulesName) {
        String name = rulesName[ctx.getRuleIndex()];
        for (int i=0;i<indentation;i++) {
            System.out.print("  ");
        }
        System.out.println(name);
        for (int i=0; i<ctx.getChildCount(); i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof RuleContext) {
                explore((RuleContext)element, indentation + 1, rulesName);
            }else{
                explore((TerminalNodeImpl)element, indentation + 1);
            }
        }
    }
    
    private void explore(TerminalNodeImpl ctx, int indentation) {
        for (int i=0;i<indentation;i++) {
            System.out.print("  ");
        }
        System.out.println(ctx);
        for (int i=0; i<ctx.getChildCount(); i++) {
            ParseTree element = ctx.getChild(i);
            explore((TerminalNodeImpl)element, indentation + 1);
        }
    }
    
    private int numParsingErrors(Object parser){
        try{
            Method method = parserClass.getMethod("getNumberOfSyntaxErrors");
            return (int) method.invoke(parser);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e){
            e.printStackTrace();
        }
        return 1; 
    } 
    
    public RuleContext parse(String code) throws Exception{
        RuleContext ctx = null;
        try{
            Constructor lexerConstructor = this.lexerClass.getConstructor(CharStream.class);
            Constructor parserConstructor = this.parserClass.getConstructor(TokenStream.class);

            Object lexer = lexerConstructor.newInstance(new ANTLRInputStream(code));
            CommonTokenStream tokens = new CommonTokenStream((TokenSource)lexer);

            this.parser = parserConstructor.newInstance(tokens);

            Method method = parserClass.getMethod("parse");
            ctx = (RuleContext)method.invoke(parser);
            
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        } catch(InstantiationException e){
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        } catch(InvocationTargetException e){
            e.printStackTrace();
        } 
        
        if(this.numParsingErrors(this.parser) > 0)
            throw new Exception("Parsing Error!");

        return ctx;
    } 
    

    public RuleContext parse(File file) throws Exception{
        try{
            String code = this.readFile(file, Charset.forName("UTF-8"));
            return this.parse(code);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
