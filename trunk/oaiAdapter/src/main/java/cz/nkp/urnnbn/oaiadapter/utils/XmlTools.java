/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

    public static Document getDocument(String url) throws IOException, ParsingException {
        return getDocument(new URL(url));
    }

    public static Document getDocument(String url, boolean status404Allowed) throws IOException, ParsingException {
        return getDocument(new URL(url), status404Allowed);
    }

    public static Document getDocument(URL url) throws IOException, ParsingException {
        return getDocument(url, false);
    }

    public static Document getTemplateDocumentFromString(String template) throws ParsingException, IOException, XSLException {
        Builder builder = new Builder();
        Document document = builder.build(template, null);
        return document;
    }

    public static Document getDocument(URL url, boolean status404Allowed) throws IOException, ParsingException {
        Builder builder = new Builder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        InputStream is = null;
        if (status404Allowed && con.getResponseCode() == 404) {
            is = con.getErrorStream();
            if (is == null) {
                throw new IOException("status 404 and server sent no useful data");
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
        return result;
    }

    public static HttpsURLConnection getAuthConnection(String login, String password,
            String urlString, String method, boolean doOutput)
            throws IOException {

        HttpsURLConnection connection = null;
        try {
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
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(doOutput);
            connection.setRequestMethod(method);
            connection.setDoInput(true);

            String userPassword = login + ":" + password;
            String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
            connection.setRequestProperty("Content-type", "application/xml");
            connection.setRequestProperty("Authorization", "Basic " + encoding);

        } catch (KeyManagementException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(XmlTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    public static void validateByXsdAsString(Document document, String xsd) throws DocumentOperationException {
        try {
            XOMUtils.loadDocumentValidByExternalXsd(document.toXML(), xsd);
        } catch (Exception ex) {
            throw new DocumentOperationException(ex.getMessage());
        }
    }

    public static String loadXmlFromFile(String xsltFile) throws Exception {
        try {
            Builder builder = new Builder();
            Document importStylesheet = builder.build(xsltFile);
            return importStylesheet.toXML();
        } catch (ParsingException ex) {
            throw new Exception("error parsing " + xsltFile, ex);
        } catch (IOException ex) {
            throw new Exception("error loading " + xsltFile, ex);
        }
    }
}