/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

/**
 *
 * @author hanis
 */
public class XmlTools {
    
    
    public static Document getDocument(URL url) throws IOException, ParsingException {
        return getDocument(url, false);
    }
    
    public static Document getDocument(URL url, boolean status404Allowed) throws IOException, ParsingException {
        System.out.println(url.toString());
        Builder builder = new Builder();     
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        InputStream is = null;
                
        if(status404Allowed && con.getResponseCode() == 404) {
            is = con.getErrorStream();
            if(is == null) {
                throw new IOException("status 404 and server send no useful data");
            }
        } else {
            is = con.getInputStream();
        }
        return builder.build(is);       
    }
    
    public static Document getDocument(String url) throws IOException, ParsingException {
        return getDocument(new URL(url));
    }

    public static Document getDocument(String url, boolean status404Allowed) throws IOException, ParsingException {
        return getDocument(new URL(url), status404Allowed);
    }
    
    
    public static void saveDocumentToFile(Document document, String path) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(path);
        Serializer ser = new Serializer(out, "UTF-8");
        ser.setIndent(2);
        ser.write(document);         
    }    
    
    
    public static Document getTransformedDocument(Document input, Document stylesheet) throws XSLException {
        //Builder builder = new Builder();
        //Document input = builder.build("http://www.example.com/input.xml");
        //Document stylesheet = builder.build("http://www.example.com/stylesheet.xsl");
        XSLTransform transform = new XSLTransform(stylesheet);
        Nodes output = transform.transform(input);
        Document result = XSLTransform.toDocument(output);
        System.out.println(result.toXML());
        return result;
    }

    
    
    
    
}
