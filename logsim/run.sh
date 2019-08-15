#!/bin/bash
mvn package && time java -jar target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar
