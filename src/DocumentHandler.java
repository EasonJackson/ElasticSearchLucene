import org.apache.lucene.document.Document;

import java.io.File;

public interface DocumentHandler {

    Document getDocument(File f) throws DocumentHandlerException;

    class DocumentHandlerException extends Exception {
    }
}