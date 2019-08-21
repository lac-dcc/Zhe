#!/bin/bash
QUERY1="select email from users where id = 1 and name='gerson'"
QUERY2="select email from users where id = (select * from table where name='alfred') and name='gerson'"
QUERY3="select email from users where id = 12 AND name='rdolfo'"
QUERY4="select other from users where id = 12"
QUERY5="select other from another where id = 12"
QUERY6="select email from users where otherid = (select * from table where name='alfred') and name='gerson'"
QUERY7="SELECT D_NEXT_O_ID   FROM DISTRICT WHERE D_W_ID = ?    AND D_ID = ?"

SERIALIZE="java -jar target/isomorph-1.0-SNAPSHOT-jar-with-dependencies.jar -q"
REDACT="java -jar target/isomorph-1.0-SNAPSHOT-jar-with-dependencies.jar -f tree.ser -q"

mvn package

echo "
INPUT QUERIES:
    - $QUERY1
    - $QUERY2"
$SERIALIZE "$QUERY1" "$QUERY2"
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"

echo "
INPUT QUERIES:
    - $QUERY1
    - $QUERY2
    - $QUERY3"
$SERIALIZE "$QUERY1" "$QUERY2" "$QUERY3"
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"

echo "
INPUT QUERIES:
    - $QUERY1
    - $QUERY2
    - $QUERY3
    - $QUERY4"
$SERIALIZE "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4"
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"

echo "
INPUT QUERIES:
    - $QUERY1
    - $QUERY2
    - $QUERY3
    - $QUERY4
    - $QUERY5"
$SERIALIZE "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5"
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"

echo "
INPUT QUERIES:
    - $QUERY1
    - $QUERY2
    - $QUERY3
    - $QUERY4
    - $QUERY5
    - $QUERY6"
$SERIALIZE "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6"

$SERIALIZE "$QUERY1" "$QUERY7" 
$REDACT "$QUERY1" "$QUERY2" "$QUERY3" "$QUERY4" "$QUERY5" "$QUERY6" "$QUERY7"

rm tree.ser
