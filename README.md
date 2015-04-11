# access-export
A Java-based and therefore platform-independent tool to export and convert Microsoft® Access® databases to various formats. 
It uses [Jackcess] to read mdb and accdb files from Access® versions 97-2010. 
Currently only exports to SQLite.

## Usage
### Build
    mvn clean package
This creates an executable JAR access-export-x.x.x.jar in the target directory.
### Run
    java -jar access-export-x.x.x.jar [-t <tables>] <source> <target>
The source must be an mdb or accdb file. The target file must not exist, it will be created.

* -t \<tables\> : a comma-separated list of tables to export

## Dependencies
* [SLF4J], licensed under [MIT License]
* [Xerial SQLite JDBC], licensed under [The Apache Software License, Version 2.0]
* [JCommander], licensed under [The Apache Software License, Version 2.0]
* [Commons Lang], licensed under [The Apache Software License, Version 2.0]
* [Jackcess], licensed under [GNU Lesser General Public License, Version 2.1]
* [Logback], licensed under [GNU Lesser General Public License, Version 2.1]

[Jackcess]: http://jackcess.sourceforge.net/
[Logback]: http://logback.qos.ch/
[SLF4J]: http://www.slf4j.org/
[Xerial SQLite JDBC]: https://bitbucket.org/xerial/sqlite-jdbc
[JCommander]: http://jcommander.org/
[Commons Lang]: http://commons.apache.org/proper/commons-lang/

[GNU Lesser General Public License, Version 2.1]: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
[MIT License]: http://opensource.org/licenses/MIT
[The Apache Software License, Version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.txt