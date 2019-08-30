#!/bin/bash
java -javaagent:target/JavaAgent-0.0.1.jar="../srini_sample/tree.ser;MySecretKey" -classpath target/JavaAgent-0.0.1.jar com.mscufmg.javaagent.Test
