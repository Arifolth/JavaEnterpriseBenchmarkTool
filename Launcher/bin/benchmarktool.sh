#!/bin/sh

#bash must be available on target system    

java -jar -Dlogback.configurationFile=./logback.xml launcher-1.0-SNAPSHOT.jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 ru.arifolth.benchmark.BenchmarkMain
