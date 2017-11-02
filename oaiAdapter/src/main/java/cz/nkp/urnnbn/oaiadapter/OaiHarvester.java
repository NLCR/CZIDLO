/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import nu.xom.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hanis
 */
public class OaiHarvester {

    private static final Logger logger = Logger.getLogger(OaiHarvester.class.getName());
    public static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;
    private Stack<String> identifiersStack;
    private boolean next = false;
    private String resumptionToken;

    public OaiHarvester(String oaiBaseUrl, String metadataPrefix) throws OaiHarvesterException {
        this(oaiBaseUrl, metadataPrefix, null);
    }

    public OaiHarvester(String oaiBaseUrl, String metadataPrefix, String setSpec) throws OaiHarvesterException {
        if (oaiBaseUrl == null) {
            throw new NullPointerException("oaiBasUrl not specified");
        }
        if (metadataPrefix == null) {
            throw new NullPointerException("metadataPrefix not specified");
        }
        this.oaiBaseUrl = oaiBaseUrl;
        this.metadataPrefix = metadataPrefix;
        this.setSpec = setSpec;

        this.next = true;
        identifiersStack = new Stack<String>();
        initHarvesting();
    }

    private boolean isSetSpecified() {
        return setSpec != null;
    }

    private String getListIdentifiersPrefixUrl() {
        return oaiBaseUrl + "?verb=ListIdentifiers";
    }

    private String addSet(String url) {
        if (isSetSpecified()) {
            return url + "&set=" + setSpec;
        }
        return url;
    }

    private String getRecordUrl(String identifier) {
        return oaiBaseUrl + "?verb=GetRecord&metadataPrefix=" + metadataPrefix + "&identifier=" + identifier;
    }

    private String getListIdentifiersUrl() {
        String url = getListIdentifiersPrefixUrl() + "&metadataPrefix=" + metadataPrefix;
        return addSet(url);
    }

    private String getResumptionTokenUrl(String token) {
        return getListIdentifiersPrefixUrl() + "&resumptionToken=" + token;
    }

    private String addIdentifiers(String url) throws OaiHarvesterException {
        Document document = null;
        try {
            document = getDocument(url);
        } catch (ParsingException ex) {
            next = false;
            throw new OaiHarvesterException("ListIdentifiers failed while parsing document.", url.toString());
        } catch (IOException ex) {
            next = false;
            throw new OaiHarvesterException("ListIdentifiers failed while fetching document.", url.toString());
        }
        Element root = document.getRootElement();
        XPathContext context = new XPathContext("oai", OAI_NAMESPACE);
        // Nodes nodes = root.query("//oai:header/oai:identifier", context);
        Nodes nodes = root.query("//oai:header", context);
        for (int i = 0; i < nodes.size(); i++) {
            Node header = nodes.get(i);
            Nodes identifiers = header.query("oai:identifier", context);
            if (identifiers == null || identifiers.size() < 1) {
                throw new OaiHarvesterException("ListIdentifiers failed - no identifier in header element", url.toString());
            }
            Node identifier = identifiers.get(0);
            String id = identifier.getValue();
            Attribute status = ((Element) header).getAttribute("status");
            if (status != null && status.getValue().equals("deleted")) {
                continue;
            }
            identifiersStack.push(id);
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

    private Document getRecordDocument(String identifier) throws OaiHarvesterException {
        String url = getRecordUrl(identifier);
        try {
            return getDocument(url);
        } catch (IOException ex) {
            throw new OaiHarvesterException("Failed downloading document from url.", url.toString());
        } catch (ParsingException ex) {
            throw new OaiHarvesterException("Failed parsing document.", url.toString());
        }
    }

    // public List<String> getListIdentifiers(int limit) throws ParsingException, IOException {
    // URL url = getListIdentifiersUrl();
    // List<String> list = new ArrayList<String>();
    // String resumptionToken = addIdentifiers(url, list, limit);
    // while (resumptionToken != null) {
    // url = getResumptionTokenUrl(resumptionToken);
    // resumptionToken = addIdentifiers(url, list, limit);
    // }
    // return list;
    // }
    private void initHarvesting() throws OaiHarvesterException {
        String url = getListIdentifiersUrl();
        String token = addIdentifiers(url);
        updateResumtionToken(token);
    }

    private void updateResumtionToken(String token) {
        if (token == null) {
            next = false;
        }
        this.resumptionToken = token;
    }

    public boolean hasNext() {
        return !identifiersStack.isEmpty() || next;
    }

    public OaiRecord getNext() throws OaiHarvesterException {
        String identifier = getNextId();
        if (identifier == null) {
            return null;
        } else {
            Document document = getRecordDocument(identifier);
            return new OaiRecord(identifier, document);
        }
    }

    private String getNextId() throws OaiHarvesterException {
        if (!hasNext()) {
            return null;
        }
        if (!identifiersStack.isEmpty()) {
            return identifiersStack.pop();
        } else {
            loadNextIdentifiers();
            return getNextId();
        }
    }

    private void loadNextIdentifiers() throws OaiHarvesterException {
        String url = getResumptionTokenUrl(resumptionToken);
        // System.out.println("url: " + url);
        String token = addIdentifiers(url);
        // System.out.println("token: " + token);
        updateResumtionToken(token);
    }

    public String getOaiBaseUrl() {
        return oaiBaseUrl;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public String getSetSpec() {
        return setSpec;
    }

    public static void main(String[] args) {
        try {
            OaiHarvester harvester = new OaiHarvester("http://kramerius.mzk.cz/oaiprovider", "oai_dc", "monograph");
            // OaiHarvester harvester = new OaiHarvester("http://duha.mzk.cz/oai", "oai_dc");
            // OaiHarvester harvester = new OaiHarvester("http://oai.mzk.cz/", "marc21",
            // "collection:mollMaps");
            int counter = 0;
            while (harvester.hasNext()) {
                Document doc = null;
                try {
                    doc = harvester.getNext().getDocument();
                } catch (OaiHarvesterException ex) {
                    logger.log(Level.SEVERE, "cannot fetch a record: " + ex.getMessage() + ", " + ex.getUrl(), ex);
                }
                if (doc != null) {
                    System.out.println(doc.toXML());
                    System.out.println("---------------------------------------------------------");
                    counter++;
                } else {
                    System.out.println("doc is null");
                }
            }
            System.out.println(counter);
        } catch (OaiHarvesterException ex) {
            logger.log(Level.SEVERE, "cannot initiate oai harvester: " + ex.getMessage() + ", " + ex.getUrl(), ex);
        }
    }


    private Document getDocument(String url) throws IOException, ParsingException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        InputStream is = con.getInputStream();
        return new Builder().build(is);
    }

}
