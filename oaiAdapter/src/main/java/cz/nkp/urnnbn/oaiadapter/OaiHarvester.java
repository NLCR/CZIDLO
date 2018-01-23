/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.api_client.v5.utils.ApiResponse;
import cz.nkp.urnnbn.api_client.v5.utils.HttpConnector;
import nu.xom.*;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class OaiHarvester {

    public static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private final String oaiBaseUrl;
    private final String metadataPrefix;
    private final String setSpec;
    private final Stack<String> identifiersStack;
    private String resumptionToken;
    private final HttpConnector httpConnector = new HttpConnector();
    private final ReportLogger reportLogger;

    public OaiHarvester(String oaiBaseUrl, String metadataPrefix, String setSpec, ReportLogger reportLogger) throws OaiHarvesterException {
        if (oaiBaseUrl == null) {
            throw new NullPointerException("oaiBasUrl not specified");
        }
        if (metadataPrefix == null) {
            throw new NullPointerException("metadataPrefix not specified");
        }
        this.oaiBaseUrl = oaiBaseUrl;
        this.metadataPrefix = metadataPrefix;
        this.setSpec = setSpec;
        this.reportLogger = reportLogger;

        this.identifiersStack = new Stack<>();
        listAndStackFirstIdentifiers();
    }

    private void listAndStackFirstIdentifiers() throws OaiHarvesterException {
        String url = setSpec == null ?
                oaiBaseUrl + "?verb=ListIdentifiers&metadataPrefix=" + metadataPrefix
                :
                oaiBaseUrl + "?verb=ListIdentifiers&metadataPrefix=" + metadataPrefix + "&set=" + setSpec;
        resumptionToken = listAndStackIdentifiers(url, true);
    }

    private String listAndStackIdentifiers(String url, boolean logTotalSize) throws OaiHarvesterException {
        try {
            Document document = fetchDocument(url);
            Element root = document.getRootElement();
            XPathContext context = new XPathContext("oai", OAI_NAMESPACE);
            // Nodes headerNodes = root.query("//oai:header/oai:identifier", context);
            Nodes headerNodes = root.query("//oai:header", context);
            for (int i = 0; i < headerNodes.size(); i++) {
                Node header = headerNodes.get(i);
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

            Nodes resumptionTokenNodes = root.query("//oai:resumptionToken", context);
            if (resumptionTokenNodes.size() > 0) {
                Element resumptionTokenEl = (Element) resumptionTokenNodes.get(0);
                if (logTotalSize) {
                    String completeListSize = resumptionTokenEl.getAttributeValue("completeListSize");
                    if (completeListSize != null) {
                        if (reportLogger != null) {
                            reportLogger.report("OAI harvester total records: " + completeListSize);
                        }
                    }
                }
                String token = resumptionTokenEl.getValue();
                if (token.isEmpty()) {
                    return null;
                }
                return token;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new OaiHarvesterException("ListIdentifiers failed while fetching document.", url.toString(), e);
        } catch (ParsingException e) {
            throw new OaiHarvesterException("ListIdentifiers failed while parsing document.", url.toString(), e);
        }

    }

    private Document fetchDocument(String url) throws ParsingException, IOException {
        ApiResponse apiResponse = httpConnector.httpGet(new URL(url), null, false);
        if (apiResponse.getHttpCode() == 200) {
            return new Builder().build(apiResponse.getBody(), null);
        } else {
            throw new IOException("HTTP " + apiResponse.getHttpCode());
        }
    }

    private Document getRecordDocument(String identifier) throws OaiHarvesterException {
        String url = oaiBaseUrl + "?verb=GetRecord&metadataPrefix=" + metadataPrefix + "&identifier=" + identifier;
        try {
            Document doc = fetchDocument(url);
            return doc;
        } catch (IOException ex) {
            throw new OaiHarvesterException("Failed downloading document from url.", url.toString(), ex);
        } catch (ParsingException ex) {
            throw new OaiHarvesterException("Failed parsing document.", url.toString(), ex);
        }
    }

    public boolean existsNextIdentifier() {
        return !identifiersStack.isEmpty() || resumptionToken != null;
    }

    public OaiRecord getNextRecord() throws OaiHarvesterException {
        String identifier = getNextIdentifier();
        if (identifier == null) {
            return null;
        } else {
            Document document = getRecordDocument(identifier);
            return new OaiRecord(identifier, document);
        }
    }

    private String getNextIdentifier() throws OaiHarvesterException {
        if (!existsNextIdentifier()) {
            return null;
        }
        if (!identifiersStack.isEmpty()) {
            return identifiersStack.pop();
        } else {
            try {
                resumptionToken = loadAndStackNextIdentifiers();
            } catch (OaiHarvesterException e) { //typically when resumption token expired
                resumptionToken = null;
                throw e;
            }
            return getNextIdentifier();
        }
    }

    private String loadAndStackNextIdentifiers() throws OaiHarvesterException {
        String url = oaiBaseUrl + "?verb=ListIdentifiers&resumptionToken=" + resumptionToken;
        String resumptionToken = listAndStackIdentifiers(url, false);
        return resumptionToken;
    }

}
