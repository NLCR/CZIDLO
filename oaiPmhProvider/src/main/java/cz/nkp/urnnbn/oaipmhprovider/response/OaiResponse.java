/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import cz.nkp.urnnbn.oaipmhprovider.conf.OaiPmhConfiguration;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class OaiResponse {

    protected final Map<String, String[]> arguments;
    protected final String verbStr;
    protected Document doc;
    private Namespace oai = Namespace.get("http://www.openarchives.org/OAI/2.0/");
    protected Namespace xsi = DocumentHelper.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    protected OaiPmhConfiguration config;

    public OaiResponse(String verbStr, Map<String, String[]> params) throws IOException {
        config = OaiPmhConfiguration.instanceOf();
        this.verbStr = verbStr;
        this.arguments = decodeParameterMap(params);
    }

    private Map<String, String[]> decodeParameterMap(Map<String, String[]> encoded) {
        Map<String, String[]> result = new HashMap<String, String[]>();
        for (String key : encoded.keySet()) {
            String keyDecoded = decodeString(key);
            result.put(keyDecoded, encoded.get(key));
        }
        return result;
    }

    private String decodeString(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OaiResponse.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public OaiResponse(Map<String, String[]> params) throws IOException {
        this(null, params);
    }

    protected void buildOaiHeader() throws IOException {
        doc = DocumentHelper.createDocument();
        Element oaiPmh = doc.addElement(new QName("OAI-PMH", oai));
        oaiPmh.addAttribute(new QName("schemaLocation", xsi), "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
        Element responseDate = oaiPmh.addElement(new QName("responseDate", oai));
        responseDate.addText(buildResponseDate());
        buildRequestEl(oaiPmh);
    }

    private String buildResponseDate() {
        return DateStamp.now().toString();
    }

    private void buildRequestEl(Element oaiPmh) throws IOException {
        Element requestEl = oaiPmh.addElement(new QName("request", oai));
        if (verbStr != null) {
            requestEl.addAttribute("verb", verbStr);
        }
        // addOtherParameters(requestEl);
        requestEl.addText(getBaseUrl());
    }

    private void addOtherParameters(Element requestEl) {
        for (String key : arguments.keySet()) {
            String[] values = arguments.get(key);
            if (isAllowed(key) && values != null && values.length == 1) {
                requestEl.addAttribute(key, values[0]);
            }
        }
    }

    boolean isAllowed(String attribute) {
        String[] allowedAttributes = { "identifier", "metadataPrefix", "from", "until", "set", "resumptionToken" };
        for (String allowedAttribute : allowedAttributes) {
            if (allowedAttribute.equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    private String getBaseUrl() throws IOException {
        return config.getBaseUrl();
    }
}
