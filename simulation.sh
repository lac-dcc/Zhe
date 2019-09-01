#!/bin/bash

# java -javaagent:Zhe/target/Zhe-0.1.jar="srini_sample/tree.ser;MySecretPassword" -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/queries_select.txt -r normal 
# java -javaagent:Zhe/target/Zhe-0.1.jar="srini_sample/tree.ser;MySecretPassword" -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l srini_sample/mysql_sample.log -r normal 
# java -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/mysql.log -s 1000 normal 
java -javaagent:Zhe/target/Zhe-0.1.jar="srini_sample/tree.ser;MySecretPassword" -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/mysql.log -s 0.1 normal 
# java -jar Zhe/target/Zhe-0.1-jar-with-dependencies.jar simulate -l analysis/queries_select.txt normal 
