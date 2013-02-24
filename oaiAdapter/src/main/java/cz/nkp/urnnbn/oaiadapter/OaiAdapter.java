/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.ImportDocumentHandler;
import cz.nkp.urnnbn.oaiadapter.utils.Refiner;
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

    public enum Mode {

        RESERVATION, BY_RESOLVER, BY_REGISTRAR
    }
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
    private Mode mode;
    private int limit = -1;
    private ReportLogger reportLogger;

    public OaiAdapter() {
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
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

    public boolean processSingleDocument(String oaiIdentifier, Document digitalDocument, Document digitalInstance) throws OaiAdapterException, ResolverConnectionException {
        Refiner.refineDocument(digitalDocument);
        ImportDocumentHandler.putRegistrarScopeIdentifier(digitalDocument, oaiIdentifier);
        String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(digitalDocument);
        if (urnnbn == null) {
            if (getMode() == Mode.BY_RESOLVER) {
                urnnbn = ResolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
                if (urnnbn == null) {
                    urnnbn = importDigitalDocument(digitalDocument, oaiIdentifier);
                }
            } else {
                throw new OaiAdapterException("Incorrect mode - document doesn't contain urnnbn and mode is not BY_RESOLVER");
            }
        } else {
            if (getMode() == Mode.BY_RESOLVER) {
                throw new OaiAdapterException("Incorrect mode - document contains urnnbn and mode is BY_RESOLVER");
            }
            ResolverConnector.UrnnbnStatus urnnbnStatus = ResolverConnector.getUrnnbnStatus(urnnbn);
            report("- urnnbn Status: " + urnnbnStatus);

            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.UNDEFINED) {
                throw new OaiAdapterException("Checking urnbn status failed");
            }
            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.RESERVED && getMode() != Mode.RESERVATION) {
                throw new OaiAdapterException("Incorrect mode - Urnnbn has status RESERVED and mode is not RESERVATION");
            }
            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.FREE && getMode() != Mode.BY_REGISTRAR) {
                throw new OaiAdapterException("Incorrect mode - Urnnbn has status FREE and mode is not BY_REGISTRAR");
            }

            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.ACTIVE) {
                String urnnbnByTriplet = ResolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
                if (!urnnbn.equals(urnnbnByTriplet)) {
                    throw new OaiAdapterException("Urnnbn in import document doesn't match urnnbn obtained by OAI_ADAPTER ID");
                }
            } else {
                importDigitalDocument(digitalDocument, oaiIdentifier);
            }
        }        
        DigitalInstance newDi = ImportDocumentHandler.getDIFromSourceDocument(digitalInstance);
        DigitalInstance oldDi = null;
        try {
            oldDi = ResolverConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
        } catch (IOException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(oldDi == null) {
            //di doesnt exist yet            
            // IMPORT            
            importDigitalInstance(digitalInstance, urnnbn, oaiIdentifier);
            report("- DI doesn't exists ...importing DI");
            return true;
        } else {
            //di already exist
            if(newDi.isChanged(oldDi)) {
                //di has been changed
                // REMOVE
                ResolverConnector.removeDigitalInstance(oldDi.getId(), login, password);
                // IMPORT
                importDigitalInstance(digitalInstance, urnnbn, oaiIdentifier);
                report("- DI already exists and is modified ...removing old one and imporing new DI");               
                return true;
            } else {
                // no change ..do nothing
                report("- DI already exists and is not modified ...doing nothing.");
                return false;
            }
        }                
    }

    private void importDigitalInstance(Document digitalInstance, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
        try {
            ResolverConnector.importDigitalInstance(digitalInstance, urnnbn, login, password);
            report("- digital instance successfully added to resolver - continue.");
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when importing digital instance. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred when importing digital instance. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException("ResolverConnectionException occurred when importing digital instance. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        }

    }

    private String importDigitalDocument(Document digitalDocument, String oaiIdentifier) throws OaiAdapterException {
        try {
            String urnnbn = ResolverConnector.importDocument(digitalDocument, registrarCode, login, password);
            report("- import successfully added to resolver - continue.");
            report("- URNNBN: " + urnnbn);
            return urnnbn;
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred when importing document. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred when importing document. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException("ResolverConnectionException occurred when importing document. "
                    + "identifier: " + oaiIdentifier
                    + ", ex: " + ex.getMessage());
        }
    }
    
    
    
    
    private boolean importDocument(Record record, Document importTemplate, Document digitalInstanceTemplate)
            throws OaiAdapterException {

        report("------------------------------------------------------");
        report("Importing document - identifier: " + record.getIdentifier());

        String identifier = record.getIdentifier();
        Document importDocument = null;
        Document digitalInstanceDocument = null;
        try {
            importDocument = XmlTools.getTransformedDocument(record.getDocument(), importTemplate);
            report("- import tranformation successful - continue.");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming import document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        try {
            XmlTools.validateImport(importDocument);
            report("- import validation successful - continue.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- import invalid - skip \nMessage: " + ex.getMessage());
        }

        try {
            digitalInstanceDocument = XmlTools.getTransformedDocument(record.getDocument(), digitalInstanceTemplate);
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
       
        try {
            return processSingleDocument(identifier, importDocument, digitalInstanceDocument);
        } catch (ResolverConnectionException ex) {
            throw new OaiAdapterException(ex.getMessage());
        }
        
    }
    
    
    
    
    
    
    public void run() {
        try {
            Document importTemplate = getImportTemplateDocument();
            Document digitalInstanceTemplate = getDigitalInstanceTemplateDocument();


            report("REPORT:");
            report(" OAI base url: " + getOaiBaseUrl());
            report(" Metadata prefix: " + getMetadataPrefix());
            report(" Set: " + (setSpec == null ? "not set" : setSpec));
            report(" Mode: " + getMode());
            report("-----------------------------------------------------");

            if(!ResolverConnector.checkRegistrarMode(getRegistrarCode(), getMode())) {
                report(" Mode " + getMode() + " is not enabled for registrar " + getRegistrarCode());
                logger.log(Level.SEVERE, "Mode {0} is not enabled for registrar {1}", new Object[]{getMode(), getRegistrarCode()});
                return;                
            }
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
                    Record record = harvester.getNext();
                    all++;
                    try {
                        boolean imported = importDocument(record, importTemplate, digitalInstanceTemplate);
                        if (imported) {
                            success++;
                            logger.log(Level.INFO, "Record successfully added. Identifier {0}", record.getIdentifier());
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
            report("SUCCESSFUL RECORDS (NEW/MODIFIED): " + success);
            report("SUCCESSFUL RECORDS:(ALREADY IMPORTED AND NOT MODIFIED): " + alreadyImported);
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
