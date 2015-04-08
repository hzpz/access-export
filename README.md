# access-export
A Java-based and therefore platform-independent tool to export and convert MS Access databases to various formats. 
It uses [Jackcess](http://jackcess.sourceforge.net/) to read mdb and accdb files from MS Access versions 97-2010. 
Currently only exports to SQLite.

## Usage
### Build an executable JAR with Maven
    mvn clean package
This creates an executable JAR access-export-x.x.x.jar in the target directory.
### Run
    java -jar access-export-x.x.x.jar <source> <target>
The source must be an mdb or accdb file. The target file must not exist, it will be created.