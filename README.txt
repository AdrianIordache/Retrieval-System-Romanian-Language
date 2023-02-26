1. JAVA VERSIONS
For this project we use as java versions:
javac -version = 1.8.0_171
java  -version = 1.8.0_171

2. FOLDER STRUCTURE
The folder structure should be:
-root\
    -code\
        -com\
            -irtm\
                -\*.java
                -\*.class
    -dependencies\
    -documentation\
        -\Romanian_Information_Retrieval_System_Iordache_Adrian_507.pdf
    -documents\
        -\a_file.txt
    -index\
    -utils\
        -\stopwords.txt
    -\query_file.txt
    -\README.txt

3. DEPENDENCIES
Dependencies used in this project:
- lucene-analyzers-common-8.10.1.jar
- lucene-core-8.10.1.jar
- lucene-demo-8.10.1.jar
- lucene-queryparser-8.10.1.jar
- pdfbox-app-2.0.24.jar
- tika-app-1.27.jar
- tika-app-2.1.0.jar
- tika-eval-app-2.1.0.jar
- tika-parser-scientific-package-2.1.0.jar
- tika-parser-sqlite3-package-2.1.0.jar
- tika-server-standard-2.1.0.jar
Those should be placed in "dependencies" folder

4. CMD COMMANDS (for Windows)
Build Project command: 
javac -classpath ".\code;.\dependencies\*" .\code\com\irtm\*.java

Run Project command:
Method One (from Indexer and Searcher Class):
Indexing: java -classpath ".\code;.\dependencies\*" com.irtm.Indexer documents index

Expected Output:
Indexing Stage
Indexing Documents from: documents
Indexing File: a_file.txt
Indexing File: ...
Indexing File: ...
Successfully Indexed Documents from: documents
Saving Index at: index

Searching: java -classpath ".\code;.\dependencies\*" com.irtm.Searcher query_file.txt index

Expected Output:
Searching Stage
Searching: demo
Found 1 hits.
1. a_file.txt
... 
The content of a_file.txt
...

Method Two (from RetrievalSystem Class):
Indexing: java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -i documents index
Searching: java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -s query_file.txt index

For help you can use:
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -h
or
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem --help

