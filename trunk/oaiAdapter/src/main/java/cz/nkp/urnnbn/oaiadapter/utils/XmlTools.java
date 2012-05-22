/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.Credentials;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author hanis
 */
public class XmlTools {

    public static Document getDocument(String url) throws IOException, ParsingException {
        return getDocument(new URL(url));
    }

    public static Document getDocument(String url, boolean status404Allowed) throws IOException, ParsingException {
        return getDocument(new URL(url), status404Allowed);
    }

    public static Document getDocument(URL url) throws IOException, ParsingException {
        return getDocument(url, false);
    }

    public static Document getDocument(URL url, boolean status404Allowed) throws IOException, ParsingException {
        System.out.println(url.toString());
        Builder builder = new Builder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        InputStream is = null;

        if (status404Allowed && con.getResponseCode() == 404) {
            is = con.getErrorStream();
            if (is == null) {
                throw new IOException("status 404 and server send no useful data");
            }
        } else {
            is = con.getInputStream();
        }
        return builder.build(is);
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
        XSLTransform transform = new XSLTransform(stylesheet);
        Nodes output = transform.transform(input);
        Document result = XSLTransform.toDocument(output);
        System.out.println(result.toXML());
        return result;
    }

    private javax.net.ssl.HttpsURLConnection getAuthConnection(String login, String password, String urlString, String method, boolean doOutput) throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }
    }};
        SSLContext sc = SSLContext.getInstance("SSL");
        HostnameVerifier hv = new HostnameVerifier() {

            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        //String urlString = "https://resolver-test.nkp.cz/api/v2/registrars/" + oai.getRegistrarCode() + "/digitalDocuments";
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(method);
        connection.setDoInput(true);

        String userPassword = login + ":" + password;
        String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
        connection.setRequestProperty("Content-type", "application/xml");
        connection.setRequestProperty("Authorization", "Basic " + encoding);        
        return connection;
    }

    public static void validateImport(Document document) throws SAXException, ParserConfigurationException, ParsingException, ValidityException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        String schemaPath = "/home/hanis/prace/resolver/urnnbn-resolver-v2/legacyRecordsImport/src/main/java/cz/nkp/urnnbn/legacyrecordsimport/validation/import.xsd";
        factory.setSchema(schemaFactory.newSchema(
                new Source[]{new StreamSource(schemaPath)}));

        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new ImportErrorHandler());

        Builder builder = new Builder(reader);
        builder.build(document.toXML(), null);
    }

    public static void main(String[] args) {
        
        String url = "https://resolver-test.nkp.cz/api/v2/resolver/urn:nbn:cz:tsh01-00000d/identifiers/OAI_Adapter";
        String login =  Credentials.LOGIN;
        
        String pass = Credentials.PASSWORD;
        try {
            HttpsURLConnection connection = new XmlTools().getAuthConnection(login, pass, url, "PUT", false);
            System.out.println(connection.getResponseCode());
            InputStream is = connection.getInputStream();
            Builder builder = new Builder();
            Document responseDocument = builder.build(is);
            System.out.println(responseDocument.toXML());
            

        } catch (ParsingException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
