/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 *
 * @author hanis
 */
public class XmlTools {
    
    
    public static Document getDocument(URL url) throws IOException, ParsingException {
        System.out.println(url.toString());
        Builder builder = new Builder();     
        URLConnection con = url.openConnection();        
        InputStream is = con.getInputStream();        
        return builder.build(is);       
    }
    
    public static Document getDocument(String url) throws IOException, ParsingException {
        return getDocument(new URL(url));
    }

    
    
    
}
