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

    private boolean containsUrnnbn(Document document) {
        return ImportDocumentHandler.getUrnnbnFromDocument(document) != null;
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

//    private boolean isDocumentAlreadyImported(String identifier) throws OaiAdapterException {
//        try {
//            if (ResolverConnector.isDocumentAlreadyImported(getRegistrarCode(), identifier, OaiAdapter.REGISTAR_SCOPE_ID)) {
//                logger.log(Level.INFO, "Document already imported, registrar code: {0}, registrar scope id: {1}, identifier: {2}",
//                        new Object[]{getRegistrarCode(), OaiAdapter.REGISTAR_SCOPE_ID, identifier});
//                report("- already imported - skip.");
//                return true;
//            }
//            report("- not imported yet - continue.");
//            return false;
//        } catch (IOException ex) {
//            throw new OaiAdapterException("IOException occurred when testing if document is already imported. "
//                    + "identifier: " + identifier
//                    + ", ex: " + ex.getMessage());
//        } catch (ParsingException ex) {
//            throw new OaiAdapterException("ParsingException occurred when testing if document is already imported. "
//                    + "identifier: " + identifier
//                    + ", ex: " + ex.getMessage());
//        }
//    }
    public void processSingleDocument(String oaiIdentifier, Document digitalDocument, Document digitalInstance) throws OaiAdapterException, ResolverConnectionException {
        Refiner.refineDocument(digitalDocument);
        ImportDocumentHandler.putRegistrarScopeIdentifier(digitalDocument, oaiIdentifier);
        try {
            XmlTools.validateImport(digitalDocument);
            System.out.println("- import validation successful - continue.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- import invalid - skip \nMessage: " + ex.getMessage());
        }
        try {
            XmlTools.validateDigitalIntance(digitalInstance);
            System.out.println("- digital instance validation successful - continue.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("- digital instance invalid - skip \nMessage: " + ex.getMessage());
        }
        String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(digitalDocument);
        System.out.println("urnnbn in doc: " + urnnbn);
        if (urnnbn == null) {
            if (getMode() == Mode.BY_RESOLVER) {
                urnnbn = ResolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
                if (urnnbn == null) {
                    urnnbn = importDigitalDocument(digitalDocument, oaiIdentifier);
                }
            } else {
                throw new OaiAdapterException("Document doesn't contain urnnbn and mode is not BY_RESOLVER");
            }
        } else {
            ResolverConnector.UrnnbnStatus urnnbnStatus = ResolverConnector.getUrnnbnStatus(urnnbn);
            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.UNDEFINED) {
                throw new OaiAdapterException("Checking urnbn status failed");
            }
            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.RESERVED && getMode() != Mode.RESERVATION) {
                throw new OaiAdapterException("Urnnbn has status RESERVED and mode is not RESERVATION");
            }
            if (urnnbnStatus == ResolverConnector.UrnnbnStatus.FREE && getMode() != Mode.BY_REGISTRAR) {
                throw new OaiAdapterException("Urnnbn has status FREE and mode is not BY_REGISTRAR");
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
        System.out.println("URNNBN: " + urnnbn);
        //From this point on the process id equils from all modes and statuses. ([check digital instances]

        String digitalLibraryId = ImportDocumentHandler.getDigitalLibraryIdFromDocument(digitalInstance);
        ResolverConnector.removeAllDigitalInstances(urnnbn, digitalLibraryId, login, password);
        importDigitalInstance(digitalInstance, urnnbn, oaiIdentifier);





    }

    private void importDigitalInstance(Document digitalInstance, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
        try {
            ResolverConnector.importDigitalInstance(digitalInstance, urnnbn, login, password);
            System.out.println("- digital instance successfully added to resolver - continue.");
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
            System.out.println("import successfully added to resolver - continue.");
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

//        if (isDocumentAlreadyImported(record.getIdentifier())) {
//            return false;
//        }
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
        Refiner.refineDocument(importDocument);
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
        System.out.println(importDocument.toXML());
        if (!containsUrnnbn(importDocument)) {
            System.out.println("doesnt contain urnnbn");
            return false;
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
