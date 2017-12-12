import org.apache.lucene.document.*;

import java.io.*;

public class PlainTextHandler implements DocumentHandler{

    @Override
    public Document getDocument(File f) throws DocumentHandlerException {

        String bodyText = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = br.readLine()) != null) {
                bodyText += " " + line;
            }
            br.close();
        } catch(IOException e) {
            throw new DocumentHandlerException();
        }

        if (!bodyText.equals("")) {
            Document doc = new Document();
            String filename = f.getName();
            int dot = filename.lastIndexOf('.');
            doc.add(new StringField("filename", filename.substring(0, dot), Field.Store.YES));
            doc.add(new TextField("body", bodyText, Field.Store .YES));
            doc.add(new StoredField("format", filename.substring(dot)));
            return doc;
        }

        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, DocumentHandlerException {
        PlainTextHandler handler = new PlainTextHandler();
        Document doc = handler.getDocument(new File("data/test.txt"));
        System.out.println(doc);

        File indexDir = new File(args[0]);
        File dataDir = new File(args[1]);
        File[] files = dataDir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.getName().endsWith(".txt"))
                doc = handler.getDocument(f);
        }
    }

}
