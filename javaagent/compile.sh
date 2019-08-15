mvn install:install-file -Dfile=../isomorph/target/isomorph-1.0-SNAPSHOT-jar-with-dependencies.jar -DgroupId=com.mscufmg.isomorph -DartifactId=isomorph -Dversion=1.0-SNAPSHOT -Dpackaging=jar
mvn package
echo "
    ...RUNNING...  
"
