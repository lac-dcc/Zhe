#!/bin/bash
time java -javaagent:../javaagent/target/JavaAgent-0.0.1.jar="../srini_sample/tree.ser;SENHA" -jar target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar ../analysis/queries_select.txt
