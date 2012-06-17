/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

/**
 *
 * @author hanis
 */
public class OaiAdapter {

    private static final Logger logger = Logger.getLogger(OaiAdapter.class.getName());
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
    private ReportLogger reportLogger;

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

    public void setOutputStream(OutputStream os) {
        this.reportLogger = new ReportLogger(os);
    }

    private Document getImportTemplateDocument() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(getMetadataToImportTemplate());
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred when making import template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred when making import template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred when making import template: " + ex.getMessage());
        }
    }

    private Document getDigitalInstanceTemplateDocument() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(getMetadataToDigitalInstanceTemplate());
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred when making digital instance template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred when making digital instance template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred when making digital instance template: " + ex.getMessage());
        }
    }

    private void report(String message) {
        if (reportLogger != null) {
            reportLogger.report(message);
        }
    }

    private boolean isDocumentAlreadyImported(String identifier) throws OaiAdapterException {
        try {
            if (ResolverConnector.isDocumentAlreadyImported(getRegistrarCode(), identifier, OaiAdapter.REGISTAR_SCOPE_ID)) {
                logger.log(Level.INFO, "Document already imported, registrar code: {0}, registrar scope id: {1}, identifier: {2}",
                        new Object[]{getRegistrarCode(), OaiAdapter.REGISTAR_SCOPE_ID, identifier});
                report("- already imported - skip.");
                return true;
            }
            report("- not imported yet - continue.");
            return false;
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when testing if document is already imported. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred when testing if document is already imported. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
    }

    private boolean importDocument(String identifier, Document document, Document importTemplate, Document digitalInstanceTemplate)
            throws OaiAdapterException {
        report("------------------------------------------------------");
        report("Importing document - identifier: " + identifier);

        if (isDocumentAlreadyImported(identifier)) {
            return false;
        }

        Document importDocument = null;
        Document digitalInstanceDocument = null;
        try {
            importDocument = XmlTools.getTransformedDocument(document, importTemplate);
            report("- import tranformation successful - continue.");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming import document. "
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
            report("- import validation successful - continue.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- import invalid - skip \nMessage: " + ex.getMessage());
        }

        try {
            digitalInstanceDocument = XmlTools.getTransformedDocument(document, digitalInstanceTemplate);
            report("- digital instance transformation successful - continue.");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming digital instance document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }

        try {
            XmlTools.validateDigitalIntance(digitalInstanceDocument);
            report("- digital instance validation successful - continue.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        }
        String urnnbn = null;
        try {
            urnnbn = ResolverConnector.importDocument(importDocument, registrarCode, login, password);
            report("- import successfully added to resolver - continue.");
            report("- URNNBN: " + urnnbn);
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException("ResolverConnectionException occurred when importing document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }

        try {
            ResolverConnector.putRegistrarScopeIdentifier(urnnbn, identifier, OaiAdapter.REGISTAR_SCOPE_ID, login, password);
            report("- registrar scope id successfully added - continue.");
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException("ResolverConnectionException occurred when putting reg scope for urnnbn. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }

        try {
            ResolverConnector.importDigitalInstance(digitalInstanceDocument, urnnbn, login, password);
            report("- digital instance successfully added to resolver - continue.");
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException("ResolverConnectionException occurred when importing digital instance. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }


        return true;
    }

    public void run() {
        try {
            Document importTemplate = getImportTemplateDocument();
            Document digitalInstanceTemplate = getDigitalInstanceTemplateDocument();

            
            report("REPORT:");
            report(" OAI base url: " + getOaiBaseUrl());
            report(" Metadata prefix: " + getMetadataPrefix());
            report(" Set: " + (setSpec == null ? "not set" : setSpec));
            report("-----------------------------------------------------");
            
            OaiHarvester harvester = null;
            try {
                harvester = new OaiHarvester(getOaiBaseUrl(), getMetadataPrefix(), getSetSpec());
            } catch (OaiHarvesterException ex) {
                report("OaiHarvester initialization failed. " + ex.getMessage() + ", url: " + ex.getMessage());
                logger.log(Level.SEVERE, "OaiHarvester initialization failed. {0}, url: {1}", new Object[]{ex.getMessage(), ex.getMessage()});
                return;
            }            
            int counter = 0;
            int success = 0;
            int all = 0;
            int alreadyImported = 0;
            while (harvester.hasNext()) {
                if (limit > 0 && counter++ >= limit) {
                    break;
                }                
                try {
                    Document recordDocument = harvester.getNext();
                    String identifier = harvester.getLastIdentifier();
                    all++;
                    try {
                        boolean result = importDocument(identifier, recordDocument, importTemplate, digitalInstanceTemplate);
                        if (result) {
                            success++;
                            logger.log(Level.INFO, "Record successfully added. Identifier {0}", identifier);
                        } else {
                            alreadyImported++;
                        }
                        report("STATUS: OK");                        
                    } catch (OaiAdapterException ex) {
                        logger.log(Level.SEVERE, ex.getMessage());
                        report(ex.getMessage());
                        report("STATUS: NOT OK");
                    }
                } catch (OaiHarvesterException ex) {
                    logger.log(Level.SEVERE, "OaiHarvester exception while getting next document: {0}, url: {1}", 
                            new Object[]{ex.getMessage(), ex.getUrl()});
                    report("OaiHarvester exception while getting next document: " + ex.getMessage() + ", url: " + ex.getUrl());
                    report("STATUS: NOT OK");
                }
            }
            report("-----------------------------------------------------");
            report("ALL RECORDS: " + all);
            report("SUCCESSFUL RECORDS (NEW): " + success);
            report("SUCCESSFUL RECORDS:(ALREADY IMPORTED): " + alreadyImported);
            report("NOT SUCCESSFUL: " + (all - (success + alreadyImported)));

            if (reportLogger != null) {
                reportLogger.close();
            }
        } catch (TemplateException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

    }

    public static void main(String[] args) {
    }
}
