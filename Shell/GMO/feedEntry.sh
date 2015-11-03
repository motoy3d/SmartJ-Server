#!/bin/bash

export JAVA=/usr/bin/java
export WEBINF=/usr/tomcat6/webapps/redsmylife/WEB-INF
export LIB=$WEBINF/lib

CLASSPATH=
for name in `ls $LIB/*.jar`; do
  CLASSPATH="${CLASSPATH}:$name"
done
export CLASSPATH=$CLASSPATH:$WEBINF/classes
$JAVA -cp $CLASSPATH com.urawaredsmylife.FeedEntrySaver $1
$JAVA -cp $CLASSPATH com.urawaredsmylife.FeedEntrySaver2 $1
