#!/bin/bash
java -javaagent:target/JavaAgent-0.0.1.jar=tree.ser -classpath target/JavaAgent-0.0.1.jar com.mscufmg.javaagent.Test
