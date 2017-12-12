import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LuceneDemo {

    private static void createIndex(Path indexDir, File dataDir, Analyzer analyzer)
            throws IOException, DocumentHandler.DocumentHandlerException {

        Directory directory = new SimpleFSDirectory(indexDir);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config); // Create new IndexWriter

        PlainTextHandler handler = new PlainTextHandler();
        File[] files = dataDir.listFiles();
        Document doc;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.getName().endsWith(".txt")) {
                doc = handler.getDocument(f);
                System.out.println("Indexing " + f.getPath());
                iwriter.addDocument(doc);
                System.out.println(doc);
            }
        }
        iwriter.close();
        directory.close();
    }

    private static void searchIndex(Path indexDir, String query, Analyzer analyzer)
            throws IOException, ParseException {

        Directory directory = new SimpleFSDirectory(indexDir);
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        //Query q = new TermQuery(new Term("body", "lucene"));
        QueryParser parser = new QueryParser("body", analyzer);
        Query q = parser.parse(query);

        TopDocs res = isearcher.search(q, 10);
        ScoreDoc[] hits = res.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = isearcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("filename") + d.get("format"));
        }

        ireader.close();
        directory.close();
    }


    public static void main(String[] args) throws IOException, DocumentHandler.DocumentHandlerException, ParseException {

        Path indexDir = Paths.get("index");
        File dataDir = new File("data");

        createIndex(indexDir, dataDir, new StandardAnalyzer());

        String query = "Lucene";
        //searchIndex(indexDir, query, new SimpleAnalyzer());
        searchIndex(indexDir, query, new StandardAnalyzer());

    }
}