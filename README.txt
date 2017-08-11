Java Enterprise Benchmark Tool
Performs simple workload tests on target machine

Build and run JDK: 1.7+

Available modules:
File transfer speed tests:
- TCP
- HTTP
- SOAP
HDD test:
- read/write ratio
XSLT transformation:
- Xalan
- JDK bundled Xalan
- Saxon
JavaDB (Derby) CRUD operations test
- Create
- Read
- Update
- Delete

Features:
- Uses configurable Logger to output messages
- Modular structure (with module auto-loading)
- XML format report

Build
    execute 'mvn clean package', it will generate an "executable" JAR file
    %ROOT%\Launcher\target\launcher-1.0-SNAPSHOT.jar

Run
    Use either script to launch program on target machine:

    %ROOT%\Launcher\target\benchmarktool.bat
    or
    %ROOT%\Launcher\target\benchmarktool.sh
