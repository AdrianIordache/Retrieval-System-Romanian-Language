# Retrieval System for Romanian Language
## Information Retrieval System based on Apache Lucene and Tika (Java implementation) 

## Usage

### 1. JAVA VERSIONS
For this project we use as java versions:
javac -version = 1.8.0_171
java  -version = 1.8.0_171

### 2. FOLDER STRUCTURE
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

### 3. DEPENDENCIES
Dependencies used in this project:
- [x] lucene-analyzers-common-8.10.1.jar
- [x] lucene-core-8.10.1.jar
- [x] lucene-demo-8.10.1.jar
- [x] lucene-queryparser-8.10.1.jar
- [x] pdfbox-app-2.0.24.jar
- [x] tika-app-1.27.jar
- [x] tika-app-2.1.0.jar
- [x] tika-eval-app-2.1.0.jar
- [x] tika-parser-scientific-package-2.1.0.jar
- [x] tika-parser-sqlite3-package-2.1.0.jar
- [x] tika-server-standard-2.1.0.jar
Those should be placed in "dependencies" folder

### 4. CMD COMMANDS (for Windows)
Build Project command: 
```bash
javac -classpath ".\code;.\dependencies\*" .\code\com\irtm\*.java
```

### Run Project command:
### Method One (from Indexer and Searcher Class):
**Indexing:**
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.Indexer documents index
```

Expected Output:
Indexing Stage
Indexing Documents from: documents
Indexing File: a_file.txt
Indexing File: ...
Indexing File: ...
Successfully Indexed Documents from: documents
Saving Index at: index

**Searching:**
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.Searcher query_file.txt index
```

Expected Output:
Searching Stage
Searching: demo
Found 1 hits.
1. a_file.txt
... 
The content of a_file.txt
...

### Method Two (from RetrievalSystem Class):
**Indexing:** 
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -i documents index
```

**Searching:**
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -s query_file.txt index
```

For help you can use:
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem -h
```
or
```bash
java -classpath ".\code;.\dependencies\*" com.irtm.RetrievalSystem --help
```

