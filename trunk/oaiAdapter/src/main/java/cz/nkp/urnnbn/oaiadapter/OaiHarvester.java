/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;
import nu.xom.xslt.XSLException;

/**
 *
 * @author hanis
 */
public class OaiHarvester {

    public static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;

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

        //oai.setOaiBaseUrl("http://oai.mzk.cz/");
        //oai.setMetadataPrefix("marc21");
        //oai.setSetSpec("collection:mollMaps");



        try {
            Builder builder = new Builder();
            Document stylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_import.xsl");
            List<String> list = oai.getListIdentifiers(100);
            for (String id : list) {
                Document doc = oai.getRecordDocument(id);
                try {
                    Document output = XmlTools.getTransformedDocument(doc, stylesheet);
                    XmlTools.saveDocumentToFile(output, "/home/hanis/prace/resolver/oai/output/" + id + ".xml");
                } catch (XSLException ex) {
                    Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (ParsingException ex) {
            Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OaiHarvester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
