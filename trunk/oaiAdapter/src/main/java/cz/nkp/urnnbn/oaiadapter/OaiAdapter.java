/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import org.xml.sax.SAXException;

/**
 *
 * @author hanis
 */
public class OaiAdapter {

    public static final String REGISTAR_SCOPE_ID = "OAI_Adapter";
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;
    private String metadataToImportTemplate;
    private String metadataToDigitalInstanceTemplate;
    private String registrarCode;
    private String login;
    private String password;
    private int limit = -1;

    public OaiAdapter() {
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

    public String getMetadataToImportTemplate() {
        return metadataToImportTemplate;
    }

    public void setMetadataToImportTemplate(String metadataToImportTemplate) {
        this.metadataToImportTemplate = metadataToImportTemplate;
    }

    public String getMetadataToDigitalInstanceTemplate() {
        return metadataToDigitalInstanceTemplate;
    }

    public void setMetadataToDigitalInstanceTemplate(String metadataToDigitalInstanceTemplate) {
        this.metadataToDigitalInstanceTemplate = metadataToDigitalInstanceTemplate;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private Document getImportTemplateDocument() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(getMetadataToImportTemplate());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred when making import template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred when making import template: " + ex.getMessage());
        }
    }

    private Document getDigitalInstanceTemplateDocument() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(getMetadataToDigitalInstanceTemplate());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred when making digital instance template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred when making digital instance template: " + ex.getMessage());
        }
    }

    private boolean importDocument(String identifier, Document document, Document importTemplate, Document digitalInstanceTemplate)
            throws ResolverConnectionException, DocumentOperationException {
        System.out.println("------------------------------------------------------");
        System.out.println("Importing document - identifier: " + identifier);
        try {
            if (ResolverConnector.isDocumentAlreadyImported(getRegistrarCode(), identifier, OaiAdapter.REGISTAR_SCOPE_ID)) {
                System.out.println("- already imported - skip.");
                return false;
            }
            System.out.println("- not imported yet - continue.");
        } catch (IOException ex) {
            throw new ResolverConnectionException("IOException occurred when testing if document is already imported. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new ResolverConnectionException("ParsingException occurred when testing if document is already imported. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        Document importDocument = null;
        Document digitalInstanceDocument = null;
        try {
            importDocument = XmlTools.getTransformedDocument(document, importTemplate);
            System.out.println("- import tranformation successful - continue.");
        } catch (XSLException ex) {
            throw new DocumentOperationException("XSLException occurred when transforming import document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
//        try {
//            XmlTools.saveDocumentToFile(importDocument, "/home/hanis/tmp/outdd.xml");
//        } catch (IOException ex) {
//            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            XmlTools.validateImport(importDocument);
        } catch (SAXException ex) {
            throw new DocumentOperationException("- import invalid - skip \nMessage: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            throw new DocumentOperationException("- import invalid - skip \nMessage: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new DocumentOperationException("- import invalid - skip \nMessage: " + ex.getMessage());
        } catch (IOException ex) {
            throw new DocumentOperationException("- import invalid - skip \nMessage: " + ex.getMessage());
        }        

        try {
            digitalInstanceDocument = XmlTools.getTransformedDocument(document, digitalInstanceTemplate);
            //System.out.println(digitalInstanceDocument.toXML());
            System.out.println("- digital instance transformation successful - continue.");
        } catch (XSLException ex) {
            throw new DocumentOperationException("XSLException occurred when transforming digital instance document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }



        try {
            XmlTools.validateDigitalIntance(digitalInstanceDocument);
            System.out.println("- import validation successful - continue.");
            System.out.println("- digital instance validation successful - continue.");
        } catch (SAXException ex) {
            throw new DocumentOperationException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            throw new DocumentOperationException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new DocumentOperationException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        } catch (IOException ex) {
            throw new DocumentOperationException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        }

        String urnnbn = null;
        try {
            urnnbn = ResolverConnector.importDocument(importDocument, registrarCode, login, password);
            System.out.println("- import successfully added to resolver - continue.");
            System.out.println("- URNNBN: " + urnnbn);
        } catch (IOException ex) {
            throw new ResolverConnectionException("IOException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new ResolverConnectionException("NoSuchAlgorithmException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (KeyManagementException ex) {
            throw new ResolverConnectionException("KeyManagementException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new ResolverConnectionException("ParsingException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        try {
            ResolverConnector.putRegistrarScopeIdentifier(urnnbn, identifier, OaiAdapter.REGISTAR_SCOPE_ID, login, password);
            System.out.println("- registrar scope id successfully added - continue.");
        } catch (NoSuchAlgorithmException ex) {
            throw new ResolverConnectionException("NoSuchAlgorithmException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (KeyManagementException ex) {
            throw new ResolverConnectionException("KeyManagementException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());

        } catch (MalformedURLException ex) {
            throw new ResolverConnectionException("MalformedURLException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());

        } catch (IOException ex) {
            throw new ResolverConnectionException("IOException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        try {
            ResolverConnector.importDigitalInstance(digitalInstanceDocument, urnnbn, login, password);
            System.out.println("- digital instance successfully added to resolver - continue.");
        } catch (IOException ex) {
            throw new ResolverConnectionException("IOException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new ResolverConnectionException("NoSuchAlgorithmException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (KeyManagementException ex) {
            throw new ResolverConnectionException("KeyManagementException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new ResolverConnectionException("ParsingException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        return true;
    }

    public void run() throws TemplateException {

        Document importTemplate = getImportTemplateDocument();
        Document digitalInstanceTemplate = getDigitalInstanceTemplateDocument();

        OaiHarvester harvester = new OaiHarvester(getOaiBaseUrl(), getMetadataPrefix(), getSetSpec());
        int counter = 0;
        int success = 0;
        int all = 0;
        int alreadyImported = 0;
        while (harvester.hasNext()) {
            if (limit > 0 && counter++ >= limit) {
                break;
            }
            all++;
            Document recordDocument = harvester.getNext();
            String identifier = harvester.getLastIdentifier();
            try {
                boolean result = importDocument(identifier, recordDocument, importTemplate, digitalInstanceTemplate);
                if (result) {
                    success++;
                } else {
                    alreadyImported++;
                }
                System.out.println("STATUS: OK");
            } catch (ResolverConnectionException ex) {
                Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("STATUS: NOT OK");
            } catch (DocumentOperationException ex) {
                //TODO
//                Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getMessage());
                System.out.println("STATUS: NOT OK");
            }
        }
        System.out.println("ALL RECORDS: " + all);
        System.out.println("SUCCESSFUL RECORDS (NEW): " + success);
        System.out.println("SUCCESSFUL RECORDS:(ALREADY IMPORTED) " + alreadyImported);
        System.out.println("NOT SUCCESSFUL " + (all - (success + alreadyImported)));

    }

    public static void main(String[] args) {
    }
}
