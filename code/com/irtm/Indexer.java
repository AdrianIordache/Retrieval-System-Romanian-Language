package com.irtm;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

class Indexer {
    public static void main(String[] args) throws IOException, TikaException, SAXException, ParseException {
        if(args.length == 2){
            System.out.println("Indexing Stage");
            String path_to_documents_dir = args[0];
            String path_to_output_index  = args[1];

            System.out.println("Indexing Documents from: " + path_to_documents_dir);;
            Indexer indexer  = new Indexer(path_to_output_index);
            boolean isLoaded = indexer.indexDocuments(path_to_documents_dir);
            if (isLoaded){
                System.out.println("Successfully Indexed Documents from: " +
                    path_to_documents_dir
                );
            }
            System.out.println("Saving Index at: " + path_to_output_index);
        } else {
            System.out.println("Invalid number of arguments");
            System.out.println("[Indexer] [PATH_TO_DOCUMENTS_DIR] [PATH_TO_OUTPUT_INDEX]");
        }
    }

    private static boolean normalize = true;
    private static IndexWriter indexWriter = null;
    private final static String DEFAULT_STOPWORDS_FILE = "utils\\stopwords.txt";

    Indexer(String outputDir) throws IOException, TikaException, SAXException {
        Path outputDirPath  = Paths.get(outputDir);
        Directory directory = FSDirectory.open(outputDirPath);

        CharArraySet updatedStopWords = this.updateStopWords();
        Analyzer romanianAnalyzer = new RomanianAnalyzer(updatedStopWords);
        IndexWriterConfig config = new IndexWriterConfig(romanianAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriter = new IndexWriter(directory, config);

    }

    Indexer(String outputDir, boolean ignoreDiacritics) throws IOException, TikaException, SAXException {
        Path outputDirPath  = Paths.get(outputDir);
        Directory directory = FSDirectory.open(outputDirPath);

        CharArraySet updatedStopWords = this.updateStopWords();
        Analyzer romanianAnalyzer = new RomanianAnalyzer(updatedStopWords);
        IndexWriterConfig config = new IndexWriterConfig(romanianAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        indexWriter = new IndexWriter(directory, config);
        normalize = ignoreDiacritics;
    }

    private Document createDocument(String documentName,
                                    String documentPath,
                                    String documentContent){

        FieldType fieldType = new FieldType(TextField.TYPE_NOT_STORED);
        fieldType.setStored(true);

        Document document = new Document();
        document.add(new Field("Filename", documentName,    fieldType));
        document.add(new Field("Path",     documentPath,    fieldType));
        document.add(new Field("Content",  documentContent, fieldType));

        return document;
    }

    private CharArraySet updateStopWords() throws IOException, TikaException, SAXException {
        File file = new File(DEFAULT_STOPWORDS_FILE);

        BodyContentHandler contentHandler = new BodyContentHandler();
        FileInputStream fileInputStream   = new FileInputStream(file);
        Metadata metadata    = new Metadata();
        ParseContext context = new ParseContext();

        TXTParser TexTParser = new TXTParser();
        TexTParser.parse(fileInputStream, contentHandler, metadata, context);

        List<String> stop_words = new ArrayList<String>();
        for (String stop_word : contentHandler.toString().split("\n")){
            if (stop_word.indexOf('#') != -1)
                continue;

            stop_words.add(stop_word);
            String normalized = stripAccents(stop_word);

            if(stop_word.equals(normalized))
                continue;

            stop_words.add(normalized);
        }

        return new CharArraySet(stop_words, true);
    }

    Boolean indexDocuments(String documentsDir) throws IOException, TikaException, SAXException {
        File   directory   = new File(documentsDir);
        File[] listOfFiles = directory.listFiles();

        if (listOfFiles == null)
            return false;

        for(File file : listOfFiles){
            System.out.println("Indexing File: " + file.getCanonicalPath());

            if(!file.isFile()){
                continue;
            }

            BodyContentHandler contentHandler = new BodyContentHandler();
            FileInputStream fileInputStream   = new FileInputStream(file);
            Metadata metadata    = new Metadata();
            ParseContext context = new ParseContext();

            String extension = FileNameUtils.getExtension(file.getAbsolutePath());
            switch (extension) {
                case "txt":
                    TXTParser TexTParser = new TXTParser();
                    TexTParser.parse(fileInputStream, contentHandler, metadata, context);
                    break;
                case "pdf":
                    PDFParser pdfParser = new PDFParser();
                    pdfParser.parse(fileInputStream, contentHandler, metadata, context);
                    break;
                case "doc":
                case "docx":
                    OOXMLParser ooxmlParser = new OOXMLParser();
                    ooxmlParser.parse(fileInputStream, contentHandler, metadata, context);
                    break;
            }

            if(normalize) {
                Document normalizedDocument = createDocument(file.getName(), file.getAbsolutePath(), NormalizationLayer(contentHandler.toString()));
                indexWriter.addDocument(normalizedDocument);
            } else {
                Document document = createDocument(file.getName(), file.getAbsolutePath(), contentHandler.toString());
                indexWriter.addDocument(document);
            }
        }

        indexWriter.close();
        return true;
    }

    public static String NormalizationLayer(String string){
        string = stripAccents(string);
        return string;
    }

    private static String stripAccents(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string;
    }
}
