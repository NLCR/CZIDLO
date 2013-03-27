/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.resolver.RegistrationMode;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnectionException;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnector;
import cz.nkp.urnnbn.oaiadapter.resolver.UrnnbnStatus;
import cz.nkp.urnnbn.oaiadapter.utils.ImportDocumentHandler;
import cz.nkp.urnnbn.oaiadapter.utils.Refiner;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.File;
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
    //OAI
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;
    //RESOLVER
    private String registrarCode;
    private RegistrationMode registrationMode;
    private ResolverConnector resolverConnector;
    //XSLT
    private String metadataToImportTemplate;
    private String metadataToDigitalInstanceTemplate;
    //XSD
    private XsdProvider xsdProvider;
    //OTHER
    private int limit = -1;
    private ReportLogger reportLogger;

    public OaiAdapter() {
    }

    public RegistrationMode getRegistrationMode() {
        return registrationMode;
    }

    public void setRegistrationMode(RegistrationMode mode) {
        this.registrationMode = mode;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOutputStream(OutputStream os) {
        this.reportLogger = new ReportLogger(os);
    }

    public ResolverConnector getResolverConnector() {
        return resolverConnector;
    }

    public void setResolverConnector(ResolverConnector resolverConnector) {
        this.resolverConnector = resolverConnector;
    }

    public XsdProvider getXsdProvider() {
        return xsdProvider;
    }

    public void setXsdProvider(XsdProvider xsdProvider) {
        this.xsdProvider = xsdProvider;
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

    public Object[] processSingleDocument(String oaiIdentifier, Document digitalDocument, Document digitalInstance) throws OaiAdapterException, ResolverConnectionException {
        Refiner.refineDocument(digitalDocument, xsdProvider.getImportXsd());
        ImportDocumentHandler.putRegistrarScopeIdentifier(digitalDocument, oaiIdentifier);
        String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(digitalDocument);
        if (urnnbn == null) {
            if (getRegistrationMode() == RegistrationMode.BY_RESOLVER) {
                urnnbn = resolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
                if (urnnbn == null) {
                    urnnbn = importDigitalDocument(digitalDocument, oaiIdentifier);
                }
            } else {
                throw new OaiAdapterException("Incorrect mode - document doesn't contain urnnbn and mode is not BY_RESOLVER");
            }
        } else {
            if (getRegistrationMode() == RegistrationMode.BY_RESOLVER) {
                throw new OaiAdapterException("Incorrect mode - document contains urnnbn and mode is BY_RESOLVER");
            }
            UrnnbnStatus urnnbnStatus = resolverConnector.getUrnnbnStatus(urnnbn);
            report("- urnnbn Status: " + urnnbnStatus);

            if (urnnbnStatus == UrnnbnStatus.UNDEFINED) {
                throw new OaiAdapterException("Checking urnbn status failed");
            }
            if (urnnbnStatus == UrnnbnStatus.RESERVED && getRegistrationMode() != RegistrationMode.BY_RESERVATION) {
                throw new OaiAdapterException("Incorrect mode - Urnnbn has status RESERVED and mode is not RESERVATION");
            }
            if (urnnbnStatus == UrnnbnStatus.FREE && getRegistrationMode() != RegistrationMode.BY_REGISTRAR) {
                throw new OaiAdapterException("Incorrect mode - Urnnbn has status FREE and mode is not BY_REGISTRAR");
            }

            if (urnnbnStatus == UrnnbnStatus.ACTIVE) {
                String urnnbnByTriplet = resolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
                if (urnnbnByTriplet != null && !urnnbn.equals(urnnbnByTriplet)) {
                    throw new OaiAdapterException("Urnnbn in import document (" + urnnbn + ") doesn't match urnnbn obtained by OAI_ADAPTER ID (" + urnnbnByTriplet + ")");
                }
            } else {
                importDigitalDocument(digitalDocument, oaiIdentifier);
            }
        }
        DigitalInstance newDi = ImportDocumentHandler.getDIFromSourceDocument(digitalInstance);
        DigitalInstance oldDi = null;
        try {
            oldDi = resolverConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
        } catch (IOException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (oldDi == null) {
            //di doesnt exist yet            
            // IMPORT            
            importDigitalInstance(digitalInstance, urnnbn, oaiIdentifier);
            report("- DI doesn't exists ...importing DI");
            return new Object[]{urnnbn, Boolean.TRUE};
        } else {
            //di already exist
            if (newDi.isChanged(oldDi)) {
                //di has been changed
                // REMOVE
                resolverConnector.removeDigitalInstance(oldDi.getId());
                // IMPORT
                importDigitalInstance(digitalInstance, urnnbn, oaiIdentifier);
                report("- DI already exists and is modified ...removing old one and imporing new DI");
                return new Object[]{urnnbn, Boolean.TRUE};
            } else {
                // no change ..do nothing
                report("- DI already exists and is not modified ...doing nothing.");
                return new Object[]{urnnbn, Boolean.FALSE};
            }
        }
    }

    private void importDigitalInstance(Document digitalInstance, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
        try {
            resolverConnector.importDigitalInstance(digitalInstance, urnnbn);
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
            String urnnbn = resolverConnector.importDocument(digitalDocument, registrarCode);
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
            report("- import transformation successful - continue.");
            //saveToTempFile(importDocument, "digitalDocument-" + record.getIdentifier(), ".xml");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming import document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        //System.err.println(importDocument.toXML());
        try {
            XmlTools.validateByXsdAsString(importDocument, xsdProvider.getImportXsd());
            report("- import validation successful - continue.");
        } catch (DocumentOperationException ex) {
            //saveToTempFile(digitalInstanceDocument, "digitalDocument-" + record.getIdentifier(), ".xml");
            throw new OaiAdapterException("- import invalid - skip \nMessage: " + ex.getMessage());
        }

        try {
            digitalInstanceDocument = XmlTools.getTransformedDocument(record.getDocument(), digitalInstanceTemplate);
            report("- digital instance transformation successful - continue.");
            //saveToTempFile(digitalInstanceDocument, "digitalInstance-" + record.getIdentifier(), ".xml");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming digital instance document. "
                    + "identifier: " + identifier
                    + ", ex: " + ex.getMessage());
        }
        //System.err.println(digitalInstanceDocument.toXML());
        try {
            XmlTools.validateByXsdAsString(digitalInstanceDocument, xsdProvider.getDigitalInstanceXsd());
            report("- digital instance validation successful - continue.");
            //saveToTempFile(digitalInstanceDocument, "digitalInstance-" + record.getIdentifier(), ".xml");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        }

        try {
            Object[] documentProcessingResult = processSingleDocument(identifier, importDocument, digitalInstanceDocument);
            String urnnbn = (String) documentProcessingResult[0];
            if (urnnbn != null) {
                report("- " + urnnbn);
            }
            return (Boolean) documentProcessingResult[1];
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
            report(" Set: " + (setSpec == null ? "not defined" : setSpec));
            report(" Mode: " + getRegistrationMode());
            report("-----------------------------------------------------");

            if (!resolverConnector.checkRegistrarMode(getRegistrarCode(), getRegistrationMode())) {
                report(" Mode " + getRegistrationMode() + " is not enabled for registrar " + getRegistrarCode());
                logger.log(Level.SEVERE, "Mode {0} is not enabled for registrar {1}", new Object[]{getRegistrationMode(), getRegistrarCode()});
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

    private void saveToTempFile(Document document, String prefix, String suffix) {
        try {
            File tmpFile = File.createTempFile(prefix, suffix);
            XmlTools.saveDocumentToFile(document, tmpFile.getAbsolutePath());
        } catch (IOException ex1) {
            logger.severe(ex1.getMessage());
        }
    }
}
