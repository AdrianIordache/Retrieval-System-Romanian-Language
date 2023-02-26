package com.irtm;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RetrievalSystem {
    public static void main(String[] args) throws IOException, TikaException, SAXException, ParseException {
        if (args.length > 0) {
            if (args[0].equals("-i") || args[0].equals("--index")){
                System.out.println("Indexing Stage");
                if (args.length != 3){
                    System.out.println("Invalid Arguments Error");
                    System.out.println("Please check again with -h or --help");
                } else {
                    String path_to_documents_dir = args[1];
                    String path_to_output_index  = args[2];

                    System.out.println("Indexing Documents from: " + path_to_documents_dir);;
                    RetrievalSystem retrievalSystem = new RetrievalSystem(path_to_output_index);
                    retrievalSystem.indexDocuments(path_to_documents_dir);
                    System.out.println("Saving Index at: " + path_to_output_index);
                }
            } else if (args[0].equals("-s") || args[0].equals("--search")) {
                System.out.println("Searching Stage");
                if (args.length != 3){
                    System.out.println("Invalid Arguments Error");
                    System.out.println("Please check again with -h or --help");
                } else {
                    String path_to_query_file = args[1];
                    String path_to_index      = args[2];

                    RetrievalSystem retrievalSystem = new RetrievalSystem(path_to_index);
                    retrievalSystem.searchDocuments(path_to_query_file);
                }

            } else if (args[0].equals("-h") || args[0].equals("--help")) {
                System.out.println("List of arguments: ");
                System.out.println("[-i] or [--index] [PATH_TO_DOCUMENTS_DIR] [PATH_TO_OUTPUT_INDEX]");
                System.out.println("[-s] or [--search] [PATH_TO_QUERY_FILE] [PATH_TO_INDEX]");
                System.out.println("[-h] or [--help] Show this message and exit");
            }else{
                System.out.println("Invalid Arguments Error");
                System.out.println("Please check again with -h or --help");
            }
        }
        else
        {
            System.out.println("Invalid Arguments Error");
            System.out.println("Please check again with -h or --help");
        }
    }

    private static boolean isLoaded          = false;
    private static boolean ignoreDiacritics  = true;
    private static String  invertedIndexPath = null;

    private static Indexer  indexer  = null;
    private static Searcher searcher = null;
    private static Logger   logger   = Logger.getLogger(Searcher.class.getName());

    RetrievalSystem(String _invertedIndexPath) throws TikaException, IOException, SAXException {
        invertedIndexPath = _invertedIndexPath;
    }

    RetrievalSystem(String _invertedIndexPath, boolean _ignoreDiacritics) throws TikaException, IOException, SAXException {
        ignoreDiacritics = _ignoreDiacritics;
        invertedIndexPath = _invertedIndexPath;
    }

    public void indexDocuments(String documentsDirPath) throws TikaException, IOException, SAXException {
        indexer  = new Indexer(invertedIndexPath);
        isLoaded = indexer.indexDocuments(documentsDirPath);
        if (isLoaded){
            System.out.println("Successfully Indexed Documents from: " +
                documentsDirPath
            );
        } else {
            logger.log(Level.WARNING, "Indexer unable to load the files");
        }
    }

    public void searchDocuments(String pathToQueryFile) throws IOException, ParseException, TikaException, SAXException {
        searcher = new Searcher(invertedIndexPath);
        isLoaded = searcher.isLoaded();
        if(isLoaded){
            String stringQuery = searcher.extractQueryFromFile(pathToQueryFile);
            if(ignoreDiacritics){
                stringQuery = Indexer.NormalizationLayer(stringQuery);
            }
            int hitsPerPage = 10;
            System.out.println("Searching: " + stringQuery);
            searcher.find(stringQuery, hitsPerPage);
        } else {
            logger.log(Level.SEVERE, "Indexer not found, check index folder");
        }
    }

    public void searchDocuments(String pathToQueryFile, int hitsPerPage) throws IOException, ParseException, TikaException, SAXException {
        searcher = new Searcher(invertedIndexPath);
        isLoaded = searcher.isLoaded();
        if(isLoaded){
            String stringQuery = searcher.extractQueryFromFile(pathToQueryFile);
            if(ignoreDiacritics){
                stringQuery = Indexer.NormalizationLayer(stringQuery);
            }
            System.out.println("Searching: " + stringQuery);
            searcher.find(stringQuery, hitsPerPage);
        } else {
            logger.log(Level.SEVERE, "Indexer not found, check index folder");
        }
    }
}
