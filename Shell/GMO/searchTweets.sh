#!/bin/bash

export JAVA=/usr/bin/java
export WEBINF=/usr/tomcat6/webapps/redsmylife/WEB-INF
export LIB=$WEBINF/lib

export CLASSPATH=$WEBINF/classes:$LIB/mysql-connector-java-5.1.23-bin.jar:$LIB/twitter4j-core-3.0.3.jar:$LIB/commons-beanutils-1.8.3.jar:$LIB/commons-dbcp-1.4.jar:$LIB/commons-dbutils-1.4.jar:$LIB/commons-lang-2.4.jar:$LIB/commons-pool-1.6.jar
$JAVA -cp $CLASSPATH com.urawaredsmylife.SearchTweetsSaver
