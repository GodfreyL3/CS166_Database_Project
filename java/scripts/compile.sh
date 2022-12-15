#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
export PATH=$JAVA_HOME/bin:$PATH

psql -h localhost -p $PGPORT gloza013_DB < /extra/gloza013/project/sql/src/triggers.sql

# compile the java program
javac -d $DIR/../classes $DIR/../src/Retail.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar Retail $USER"_DB" $PGPORT $USER

