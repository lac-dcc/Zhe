#!/bin/bash
mvn install:install-file -Dfile=../javaagent/target/JavaAgent-0.0.1.jar -DgroupId=com.mscufmg.javaagent -DartifactId=JavaAgent -Dversion=0.0.1 -Dpackaging=jar
mvn package && time java -jar target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar ../analysis/queries_select.txt
