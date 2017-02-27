#!/bin/sh

#bash must be available on target system    

java -version:1.6 -jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./ -Xms512m -Xmx1280m -Dlogback.configurationFile=./logback.xml -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 launcher-1.0-SNAPSHOT.jar ru.arifolth.benchmark.BenchmarkMain
