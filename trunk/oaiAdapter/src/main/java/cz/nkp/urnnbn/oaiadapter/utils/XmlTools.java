/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

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
    
    
    
}
