package com.irtm;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Searcher {
    public static void main(String[] args) throws IOException, TikaException, SAXException, ParseException {
        if(args.length == 2){
            System.out.println("Searching Stage");
            String path_to_query_file = args[0];
            String path_to_index      = args[1];

            Searcher searcher  = new Searcher(path_to_index);

            String queryString = Indexer.NormalizationLayer(
                searcher.extractQueryFromFile(path_to_query_file)
            );

            System.out.println("Searching: " + queryString);
            searcher.find(queryString, 10);
        } else {
            System.out.println("Invalid number of arguments");
            System.out.println("[Searcher] [PATH_TO_QUERY_FILE] [PATH_TO_INDEX]");
        }
    }

    private static boolean isLoaded = false;
    private static IndexSearcher indexSearcher = null;
    private Analyzer romanianAnalyzer = new RomanianAnalyzer();

    Searcher(String indexDir) throws IOException {
        Path indexDirPath = Paths.get(indexDir);
        Directory directory = FSDirectory.open(indexDirPath);
        isLoaded = DirectoryReader.indexExists(directory);
        if (isLoaded){
            DirectoryReader directoryReader = DirectoryReader.open(directory);
            indexSearcher = new IndexSearcher(directoryReader);
        }
    }

    public boolean isLoaded(){ return isLoaded; }

    String extractQueryFromFile(String pathToQueryFile) throws IOException, TikaException, SAXException {
        File file = new File(pathToQueryFile);

        BodyContentHandler contentHandler = new BodyContentHandler();
        FileInputStream fileInputStream   = new FileInputStream(file);
        Metadata metadata    = new Metadata();
        ParseContext context = new ParseContext();

        TXTParser TexTParser = new TXTParser();
        TexTParser.parse(fileInputStream, contentHandler, metadata, context);
        return contentHandler.toString();
    }

    void find(String stringQuery, int hitsPerPage) throws ParseException, IOException {
        Query query = new QueryParser("Content", romanianAnalyzer).parse(stringQuery);
        TopDocs returns = indexSearcher.search(query, hitsPerPage);
        ScoreDoc[] hits = returns.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for(int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println((i + 1) + ". " + doc.get("Filename") + "\t" + doc.get("Content"));
        }
    }
}
