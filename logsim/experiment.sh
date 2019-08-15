#!/bin/bash
echo "ID, TIME, AGENT_PRESENT?, NUM_DIFF_LINES, NUM_OUTPUT_LINES" >> exp.csv

for i in $(seq 10); do
    echo "SEM AGENTE"
    utime=`(time -p java -jar target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar > out.txt) 2>&1 | grep real | sed 's/[^0-9.]//g'`
    echo $utime

    echo "COM AGENTE"
    utime_agent=`(time -p java -javaagent:../javaagent/target/JavaAgent-0.0.1.jar=../srini_sample/tree.ser -jar target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar > out_agent.txt) 2>&1 | grep real | sed 's/[^0-9.]//g'`
    echo $utime_agent
    
    num_diff_lines=`diff -U 0 out.txt out_agent.txt | grep "^+" | wc -l | sed 's/[^0-9]//g'`
    num_diff_lines=`expr $num_diff_lines - 2`

    echo "$i, $utime, 0, $num_diff_lines, $(wc -l out.txt | sed 's/[^0-9]//g')" >> exp.csv
    echo "$i, $utime_agent, 1, $num_diff_lines, $(expr $(wc -l out_agent.txt | sed 's/[^0-9]//g') - 1)" >> exp.csv

done

rm out.txt
rm out_agent.txt
