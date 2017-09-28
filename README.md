# Java Enterprise Benchmark Tool
This application performs simple workload tests to assess host capabilities it is running on

## Pre-requisites:
This project requires Java 7+

##Available modules:
###File transfer speed tests:
- TCP
- HTTP
- SOAP
###HDD test:
- read/write ratio
###XSLT transformation:
- Xalan
- JDK bundled Xalan
- Saxon
###JavaDB (Derby) CRUD operations test
- Create
- Read
- Update
- Delete
###Memory speed test:
- Arrays.copyOf
- System.arraycopy
###JMS sending/receiving speed test:
- HornetQ

##Features:
- Uses configurable Logger to output messages
- Modular structure (with module auto-loading and submodules etc.)
- XML format report

##Build   
Execute

    mvn clean package
    
It will generate an "executable" JAR file

    %ROOT%\Launcher\target\launcher-1.0-SNAPSHOT.jar
    
##Run

Change current working dir to 

    %ROOT%\Launcher\target\
    
folder and use either script to launch program:

Win platforms: 

    benchmarktool.bat
    
Unix platforms

    benchmarktool.sh

##Result

Look at report

    BenchmarkResult.xml

##TODO:

- externalized configuration
- JavascriptEngine benchmark
- more hdd benchmarks

## License and Copyright
This project is released under [LGPL-3](http://www.gnu.org/licenses/lgpl-3.0.html)

Copyright (C) 2017  Alexander Nilov
