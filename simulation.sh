#!/bin/bash

java -javaagent:Zhe/target/Zhe-0.1.jar="srini_sample/tree.ser;MySecretPassword" -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/queries_select.txt normal 
# java -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/queries_select.txt normal 
