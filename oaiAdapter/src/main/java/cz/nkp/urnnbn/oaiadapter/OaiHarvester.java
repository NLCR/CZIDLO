/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
import nu.xom.xslt.XSLException;
import org.xml.sax.SAXException;

/**
 *
 * @author hanis
 */
public class OaiHarvester {

    public static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;
    private String registrarCode;     
    private String login;
    private String password; 

    public OaiHarvester() {
    }

    private String getListIdentifiersPrefixUrl() {
        return oaiBaseUrl + "?verb=ListIdentifiers";
    }

    private String addSet(String url) {
        if (setSpec != null) {
            return url + "&set=" + setSpec;
        }
        return url;
    }

    private URL getRecordUrl(String identifier) throws MalformedURLException {
        String url = oaiBaseUrl + "?verb=GetRecord&metadataPrefix=" + metadataPrefix
                + "&identifier=" + identifier;
        return new URL(url);
    }

    private URL getListIdentifiersUrl() throws MalformedURLException {
        String url = getListIdentifiersPrefixUrl()
                + "&metadataPrefix=" + metadataPrefix;
        return new URL(addSet(url));
    }

    private URL getResumptionTokenUrl(String token) throws MalformedURLException {
        String url = getListIdentifiersPrefixUrl() + "&resumptionToken=" + token;
        return new URL(url);
    }

    private String addIdentifiers(URL url, List<String> list, int limit) throws ParsingException, IOException {
        Document document = XmlTools.getDocument(url);
        Element root = document.getRootElement();

        XPathContext context = new XPathContext("oai", OAI_NAMESPACE);
        Nodes nodes = root.query("//oai:header/oai:identifier", context);
        for (int i = 0; i < nodes.size(); i++) {
            if (list.size() == limit) {
                return null;
            }
            list.add(nodes.get(i).getValue());
        }
        Nodes resumption = root.query("//oai:resumptionToken", context);
        if (resumption.size() > 0) {
            String token = resumption.get(0).getValue();
            if (token.isEmpty()) {
                return null;
            }
            return token;
        }
        return null;
    }

    public Document getRecordDocument(String identifier) throws IOException, ParsingException {
        URL url = getRecordUrl(identifier);
        Document document = XmlTools.getDocument(url);
        return document;
        //Element root = document.getRootElement();
        //System.out.println(root.toXML().toString());                
    }

    public List<String> getListIdentifiers(int limit) throws ParsingException, IOException {
        URL url = getListIdentifiersUrl();
        List<String> list = new ArrayList<String>();
        String resumptionToken = addIdentifiers(url, list, limit);
        while (resumptionToken != null) {
            url = getResumptionTokenUrl(resumptionToken);
            resumptionToken = addIdentifiers(url, list, limit);
        }
        return list;
    }

    public List<String> getListIdentifiers() throws ParsingException, IOException {
        return getListIdentifiers(-1);
    }

    public String getOaiBaseUrl() {
        return oaiBaseUrl;
    }

    public void setOaiBaseUrl(String oaiBaseUrl) {
        this.oaiBaseUrl = oaiBaseUrl;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public String getSetSpec() {
        return setSpec;
    }

    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }

    public static void main(String[] args) {
        OaiHarvester oai = new OaiHarvester();
        oai.setOaiBaseUrl("http://kramerius.mzk.cz/oaiprovider/");
        oai.setMetadataPrefix("oai_dc");
        oai.setSetSpec("monograph");
        oai.setLogin(Credentials.LOGIN);
        oai.setPassword(Credentials.PASSWORD);
        oai.setRegistrarCode("tsh01");
        

        //oai.setOaiBaseUrl("http://oai.mzk.cz/");
        //oai.setMetadataPrefix("marc21");
        //oai.setSetSpec("collection:mollMaps");



        try {
            Builder builder = new Builder();
            Document stylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_import.xsl");
            Document stylesheetDI = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_digital_instance.xsl");
            List<String> list = oai.getListIdentifiers(15);            
            int i = 0;
            String id = list.get(14);
//            for (String id : list) {
//                if (i++ <6) {
//                    continue;
//                }
                System.out.println( "is there? " + ResolverConnector.isDocumentAlreadyImported(oai.getRegistrarCode(), id));
                Document doc = oai.getRecordDocument(id);
                try {
                    Document output = XmlTools.getTransformedDocument(doc, stylesheet);
                    Document outputDI = XmlTools.getTransformedDocument(doc, stylesheetDI);
                    try {
                        XmlTools.validateImport(output);
                    } catch (SAXException ex) {
                        Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ValidityException ex) {
                        Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Document outputDI = XmlTools.getTransformedDocument(doc, stylesheetDI);
//
//                    SSLContext ctx = SSLContext.getInstance("TLS");
//                    ctx.init(new KeyManager[0], new TrustManager[]{new X509TrustManager() {
//
//                            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                            }
//
//                            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                            }
//
//                            public X509Certificate[] getAcceptedIssuers() {
//                                return null;
//                            }
//                        }}, new SecureRandom());
//                    SSLContext.setDefault(ctx);


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
                    String urlString = "https://resolver-test.nkp.cz/api/v2/resolver/urn:nbn:cz:tsh01-00000e/digitalInstances";
                    //String xml = output.toXML();
                    String xml = outputDI.toXML();
                    
                    System.out.println("----------------------------");                    
                    System.out.println(xml);
                    System.out.println("----------------------------");
                    
                    URL url = new URL(urlString);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");                    
                    connection.setDoInput(true);
                    
                    String userPassword = oai.getLogin() + ":" + oai.getPassword();		
                    String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
                    connection.setRequestProperty("Content-type", "application/xml");
                    //connection.setRequestProperty("Content-Type",
				//"application/x-www-form-urlencoded");
                    connection.setRequestProperty("Authorization", "Basic " + encoding);                    
                    
//                    connection.setHostnameVerifier(new HostnameVerifier() {
//
//                        @Override
//                        public boolean verify(String arg0, SSLSession arg1) {
//                            return true;
//                        }
//                    });

                    
                    
//                    connection.setUseCaches(false);
//                    connection.setAllowUserInteraction(false);
//                    connection.setRequestProperty("Authorization",
//                            "Basic " + new sun.misc.BASE64Encoder().encode(userPassword.getBytes()));
//



                    //String xml = output.toXML();
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(xml);
                    wr.flush();
                    wr.close();
                    System.out.println("code" + connection.getResponseCode());


                    // Get the response
//    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//    String line;
//    while ((line = rd.readLine()) != null) {
//        System.out.println(line);
//    }
//    
//    rd.close();                    
//                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
//                    wr.write(xml);
//                    wr.flush();
//                    wr.close();
                      InputStream is = connection.getInputStream();
                      Document responseDocument = builder.build(is);
                     // String s = ResolverConnector.getAllocatedURNNBN(responseDocument);
                     // System.out.println("allocated urnnbn = " + s);
                      System.out.println(responseDocument.toXML());
//
















                    XmlTools.saveDocumentToFile(output, "/home/hanis/prace/resolver/oai/output/" + id + ".xml");
                    //XmlTools.saveDocumentToFile(outputDI, "/home/hanis/prace/resolver/oai/output/" + id + "_di.xml");
                } catch (KeyManagementException ex) {
                    Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (XSLException ex) {
                    Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                }
          //  }
        } catch (ParsingException ex) {
            Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getRegistrarCode() {
        return registrarCode;
    }

    public void setRegistrarCode(String registrarCode) {
        this.registrarCode = registrarCode;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
