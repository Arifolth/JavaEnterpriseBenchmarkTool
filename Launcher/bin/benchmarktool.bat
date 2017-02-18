@echo off

setlocal

for /f "tokens=*" %%a in ('dir /b *.jar') do call :concat %%a

echo clp: %clp%
java -jar launcher-1.0-SNAPSHOT.jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 -cp %clp% ^
ru.arifolth.benchmark.BenchmarkMain
goto :eof

:concat
if defined clp (
    set clp=%clp%;%1
) else (
    set clp=%1
)
goto :eof





