#!/bin/bash

# SETING EXPERIMENT UP
QUERY_FILE_PATH="../analysis/"
QUERY_FILE="queries_select.txt"
LASTRUN=`ls results | sed 's/run//' | sort -n | tail -1 | bc`
NEXTRUN=`expr $LASTRUN + 1`
EXPERIMENT_DIR="results/run$NEXTRUN"
mkdir $EXPERIMENT_DIR

# COPING DEPENDENCIES
cp -r ../javaagent/target $EXPERIMENT_DIR/target
cp target/logsim-1.0-SNAPSHOT-jar-with-dependencies.jar $EXPERIMENT_DIR
cp ../srini_sample/tree.ser $EXPERIMENT_DIR
cp "$QUERY_FILE_PATH/$QUERY_FILE" $EXPERIMENT_DIR
cp -r "exp_scripts/" $EXPERIMENT_DIR/exp_scripts
cd $EXPERIMENT_DIR
mkdir "logs"
mkdir "charts"
echo "ID, TIME, AGENT_PRESENT?, NUM_DIFF_LINES, NUM_OUTPUT_LINES, NUM_STRING_EVENTS, NUM_SQL_QUERIES, DISTRIBUTION" >> exp.csv

for i in $(seq 10); do
    echo "SEM AGENTE"
    utime=`(time -p java -jar logsim-1.0-SNAPSHOT-jar-with-dependencies.jar $QUERY_FILE > out.txt 2> stats.csv) 2>&1 | grep real | sed 's/[^0-9.]//g'`
    no_agent_stats=`cat stats.csv >&1`
    echo $utime

    echo "COM AGENTE"
    utime_agent=`(time -p java -javaagent:target/JavaAgent-0.0.1.jar=tree.ser -jar logsim-1.0-SNAPSHOT-jar-with-dependencies.jar $QUERY_FILE > out_agent.txt 2> stats.csv) 2>&1 | grep real | sed 's/[^0-9.]//g'`
    agent_stats=`cat stats.csv >&1`
    echo $utime_agent
    
    num_diff_lines=`diff -U 0 out.txt out_agent.txt | grep "^+" | wc -l | sed 's/[^0-9]//g'`
    num_diff_lines=`expr $num_diff_lines - 2`

    echo "$i, $utime, 0, $num_diff_lines, $(wc -l out.txt | sed 's/[^0-9]//g'), $no_agent_stats" >> exp.csv
    echo "$i, $utime_agent, 1, $num_diff_lines, $(expr $(wc -l out_agent.txt | sed 's/[^0-9]//g') - 1), $agent_stats" >> exp.csv
    RUN_LOG_DIR="logs/id$i"
    mkdir $RUN_LOG_DIR
    mv out.txt $RUN_LOG_DIR
    mv out_agent.txt $RUN_LOG_DIR
done

for s in $(ls exp_scripts); do
     `Rscript exp_scripts/$s`
done
