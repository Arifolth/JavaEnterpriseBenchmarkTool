@echo off

setlocal

set JAVA_HOME=C:\jdk1.7
set PATH=%JAVA_HOME%\bin;%PATH%

for /f "tokens=*" %%a in ('dir /b *.jar') do call :concat %%a

echo clp: %clp%
java -jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./ -Xms512m -Xmx1512m -Dlogback.configurationFile=./logback.xml -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 launcher-1.0-SNAPSHOT.jar -cp %clp% ^
ru.arifolth.benchmark.BenchmarkMain
goto :eof

:concat
if defined clp (
    set clp=%clp%;%1
) else (
    set clp=%1
)
goto :eof





