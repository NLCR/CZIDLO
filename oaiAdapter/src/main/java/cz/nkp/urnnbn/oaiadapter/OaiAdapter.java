/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties;
import cz.nkp.urnnbn.oaiadapter.utils.*;
import cz.nkp.urnnbn.xml.apiv4.builders.request.DiCreateBuilderXml;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.oaiadapter.RecordResult.DigitalDocumentStatus;
import cz.nkp.urnnbn.oaiadapter.RecordResult.DigitalInstanceStatus;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloConnectionException;
import cz.nkp.urnnbn.oaiadapter.czidlo.UrnnbnStatus;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class OaiAdapter {

    private static final Logger logger = Logger.getLogger(OaiAdapter.class.getName());
    public static final String REGISTAR_SCOPE_ID_TYPE = "OAI_Adapter";
    // OAI
    private String oaiBaseUrl;
    private String metadataPrefix;
    private String setSpec;
    // CZIDLO API
    private String registrarCode;
    private UrnNbnRegistrationMode registrationMode;
    private CzidloApiConnector czidloConnector;
    // XSLT
    private String metadataToDdRegistrationXslt;
    private File metadataToDdRegistrationXsltFile;
    private String metadataToDiImportXslt;
    private File metadataToDiImportXsltFile;
    // XSD
    private XsdProvider xsdProvider;
    // DI
    private boolean mergeDigitalInstances = DefinedProperties.DI_IMPORT_MERGE_DIS_DEFAULT;
    private boolean ignoreDifferenceInDiAccessibility = DefinedProperties.DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT;
    private boolean ignoreDifferenceInDiFormat = DefinedProperties.DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT;
    // OTHER
    private int limit = -1;
    //private int limit = 12;//dev only
    private ReportLogger reportLogger;

    public OaiAdapter() {
    }

    public UrnNbnRegistrationMode getRegistrationMode() {
        return registrationMode;
    }

    public void setRegistrationMode(UrnNbnRegistrationMode mode) {
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

    public void setMetadataToDdRegistrationXslt(File xsltFile, String xslt) {
        this.metadataToDdRegistrationXsltFile = xsltFile;
        this.metadataToDdRegistrationXslt = xslt;
    }

    public void setMetadataToDiImportXslt(File xsltFile, String xslt) {
        this.metadataToDiImportXsltFile = xsltFile;
        this.metadataToDiImportXslt = xslt;
    }

    public void setMergeDigitalInstances(boolean mergeDigitalInstances) {
        this.mergeDigitalInstances = mergeDigitalInstances;
    }


    public void setIgnoreDifferenceInDiAccessibility(boolean ignoreDifferenceInDiAccessibility) {
        this.ignoreDifferenceInDiAccessibility = ignoreDifferenceInDiAccessibility;
    }

    public void setIgnoreDifferenceInDiFormat(boolean ignoreDifferenceInDiFormat) {
        this.ignoreDifferenceInDiFormat = ignoreDifferenceInDiFormat;
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

    public CzidloApiConnector getCzidloApiConnector() {
        return czidloConnector;
    }

    public void setCzidloConnector(CzidloApiConnector czidloConnector) {
        this.czidloConnector = czidloConnector;
    }

    public XsdProvider getXsdProvider() {
        return xsdProvider;
    }

    public void setXsdProvider(XsdProvider xsdProvider) {
        this.xsdProvider = xsdProvider;
    }

    private Document getDigDocRegistrationTemplateDoc() throws TemplateException {
        try {
            //TODO: cache, no need to parse it again for every single record
            return XmlTools.getTemplateDocumentFromString(metadataToDdRegistrationXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building Digital-document-registration template: " + ex.getMessage());
        }
    }

    private Document getDigInstImportTemplateDoc() throws TemplateException {
        try {
            //TODO: cache, no need to parse it again for every single record
            return XmlTools.getTemplateDocumentFromString(metadataToDiImportXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building Digital-instance-import template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building Digital-instance-import template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building Digital-instance-import template: " + ex.getMessage());
        }
    }

    private void report(String message) {
        if (reportLogger != null) {
            reportLogger.report(message);
        }
    }

    private void report(String message, Throwable e) {
        if (reportLogger != null) {
            reportLogger.report(message, e);
        }
    }

    public RecordResult processSingleRecord(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData)
            throws OaiAdapterException, CzidloConnectionException {
        Refiner.refineDocument(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
        DdRegistrationDataHelper docHelper = new DdRegistrationDataHelper(digDocRegistrationData);
        docHelper.putRegistrarScopeIdentifier(oaiIdentifier);
        String urnnbn = docHelper.getUrnnbnFromDocument();
        if (urnnbn == null) {
            if (getRegistrationMode() == UrnNbnRegistrationMode.BY_RESOLVER) {
                urnnbn = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
                if (urnnbn == null) {
                    urnnbn = registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                    return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, DigitalDocumentStatus.NOW_REGISTERED);
                } else {
                    throw new OaiAdapterException(String.format("Cannot find urn:nbn by registrar-scope id %1 -> %2!",
                            OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier));
                }
            } else {
                throw new OaiAdapterException(String.format("Incorrect mode - document doesn't contain URN:NBN and mode is not %s!",
                        UrnNbnRegistrationMode.BY_RESOLVER));
            }
        } else {
            if (getRegistrationMode() == UrnNbnRegistrationMode.BY_RESOLVER) {
                throw new OaiAdapterException(String.format("Incorrect mode - document contains URN:NBN and mode is %s!",
                        UrnNbnRegistrationMode.BY_RESOLVER));
            }
            UrnnbnStatus urnnbnStatus = czidloConnector.getUrnnbnStatus(urnnbn);
            report("- " + urnnbn);
            report("- URN:NBN status: " + urnnbnStatus);
            switch (urnnbnStatus) {
                case RESERVED:
                    if (getRegistrationMode() != UrnNbnRegistrationMode.BY_RESERVATION) {
                        throw new OaiAdapterException(String.format("Incorrect mode - URN:NBN has status %s and mode is not %s!", Status.RESERVED,
                                UrnNbnRegistrationMode.BY_RESERVATION));
                    } else {
                        registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, DigitalDocumentStatus.NOW_REGISTERED);
                    }
                case FREE:
                    if (getRegistrationMode() != UrnNbnRegistrationMode.BY_REGISTRAR) {
                        throw new OaiAdapterException(String.format("Incorrect mode - URN:NBN has status %s and mode is not %s!", Status.FREE,
                                UrnNbnRegistrationMode.BY_REGISTRAR));
                    } else {
                        registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, DigitalDocumentStatus.NOW_REGISTERED);
                    }
                case ACTIVE:
                    String urnnbnByRegistrarScopeId = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE,
                            oaiIdentifier);
                    if (urnnbnByRegistrarScopeId != null && !urnnbn.equals(urnnbnByRegistrarScopeId)) {
                        throw new OaiAdapterException(String.format(
                                "URN:NBN in digital-document-registration data (%s) doesn't match URN:NBN obtained by OAI_ADAPTER ID (%s)!", urnnbn,
                                urnnbnByRegistrarScopeId));
                    } else {
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, DigitalDocumentStatus.ALREADY_REGISTERED);
                    }
                case DEACTIVATED:
                    return new RecordResult(urnnbn, DigitalDocumentStatus.IS_DEACTIVATED, null);
                case UNDEFINED:
                    throw new OaiAdapterException("Checking URN:NBN status failed");
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private RecordResult processDigitalInstance(String urnnbn, String oaiIdentifier, Document diImportData, DigitalDocumentStatus ddStatus)
            throws OaiAdapterException, CzidloConnectionException {
        DigitalInstance newDi = DiBuilder.buildDiFromImportDigitalInstanceRequest(diImportData);
        //report(diImportData.toXML());
        DigitalInstance currentDi = null;
        try {
            currentDi = czidloConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
        } catch (IOException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (currentDi == null) {
            // DI doesnt exist yet
            report("- DI doesn't exists - creating new DI ...");
            // import DI
            importDigitalInstance(diImportData, urnnbn, oaiIdentifier);
            report("- New DI created.");
            return new RecordResult(urnnbn, ddStatus, DigitalInstanceStatus.IMPORTED);
        } else {
            // DI already exist
            if (!equals(currentDi, newDi, ignoreDifferenceInDiFormat, ignoreDifferenceInDiAccessibility)) {
                // current DI is different from new DI
                report("- DI already exists and is considered different from new DI.");
                report("- Current DI: " + currentDi.toString());
                report("- New DI: " + newDi.toString());
                if (mergeDigitalInstances) {
                    DigitalInstance mergedDi = merge(newDi, currentDi);
                    diImportData = new Document(new DiCreateBuilderXml(mergedDi).buildRootElement());
                    report("- Merged DI: " + mergedDi.toString());
                }
                // deactivate current DI
                report("- Deactivating current DI ...");
                czidloConnector.deactivateDigitalInstance(currentDi.getId());
                report("- Current DI deactivated.");
                // import new (possibly merged) DI
                if (mergeDigitalInstances) {
                    report("- Creating another DI (from new DI merged with old DI) ...");
                } else {
                    report("- Creating another DI (from new DI) ...");
                }
                importDigitalInstance(diImportData, urnnbn, oaiIdentifier);
                report("- Another DI created.");
                return new RecordResult(urnnbn, ddStatus, DigitalInstanceStatus.UPDATED);
            } else {
                // no change - do nothing
                report("- DI already exists and is not considered different from new DI - doing nothing.");
                return new RecordResult(urnnbn, ddStatus, DigitalInstanceStatus.UNCHANGED);
            }
        }
    }

    private DigitalInstance merge(DigitalInstance newDi, DigitalInstance currentDi) {
        DigitalInstance merged = new DigitalInstance();
        merged.setLibraryId(newDi.getLibraryId());
        merged.setUrl(newDi.getUrl());
        //format
        if (newDi.getAccessibility() == null || newDi.getAccessibility().isEmpty()) {
            merged.setAccessibility(currentDi.getAccessibility());
        } else {
            merged.setAccessibility(newDi.getAccessibility());
        }
        //accessiblity
        if (newDi.getFormat() == null || newDi.getFormat().isEmpty()) {
            merged.setFormat(currentDi.getFormat());
        } else {
            merged.setFormat(newDi.getFormat());
        }
        return merged;
    }

    private boolean equals(DigitalInstance currentDi, DigitalInstance newDi, boolean ignoreDifferentDiFormat, boolean ignoreDifferentDiAccessibility) {
        if (!equals(currentDi.getUrl(), newDi.getUrl())) {
            return false;
        }
        if (!ignoreDifferentDiFormat && !equals(currentDi.getFormat(), newDi.getFormat())) {
            return false;
        }
        if (!ignoreDifferentDiAccessibility && !equals(currentDi.getAccessibility(), newDi.getAccessibility())) {
            return false;
        }
        return true;
    }

    private boolean equals(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        } else if (first != null && second != null && first.equals(second)) {
            return true;
        } else {
            return false;
        }
    }

    private void importDigitalInstance(Document diImportData, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
        try {
            czidloConnector.importDigitalInstance(diImportData, urnnbn);
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred during digital-instance-import:", ex);
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred during Digital-instance-import:", ex);
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("CzidloConnectionException occurred during Digital-instance-import:", ex);
        }
    }

    private String registerDigitalDocument(Document digDocRegistrationData, String oaiIdentifier) throws OaiAdapterException {
        try {
            String urnnbn = czidloConnector.registerDigitalDocument(digDocRegistrationData, registrarCode);
            report("- Digital-document-registration successful - continuing.");
            return urnnbn;
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred during Digital-document-registration:", ex);
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred during Digital-document-registration:", ex);
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("CzidloConnectionException occurred during Digital-document-registration:", ex);
        }
    }

    private RecordResult processRecord(OriginalRecordFromOai originalRecord, Document digDocRegistrationTemplate, Document digInstImportTemplate)
            throws OaiAdapterException {
        report("------------------------------------------------------");
        String oaiIdentifier = originalRecord.getIdentifier();
        report("Processing next record - identifier: " + oaiIdentifier);

        // DIGITAL DOCUMENT REGISTRATION
        Document digDocRegistrationData = null;
        try {
            digDocRegistrationData = XmlTools.getTransformedDocument(originalRecord.getDocument(), digDocRegistrationTemplate);
            report("- OAI record successfuly transformed into digital-document-registration data - continuing.");
            // saveToTempFile(importDocument, "digitalDocument-" + record.getIdentifier(), ".xml");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming record into digital-document-registration data:", ex);
        }
        try {
            XmlTools.validateByXsdAsString(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
            report("- Digital-document-registration data validation successful - continuing.");
        } catch (DocumentOperationException ex) {
            // saveToTempFile(digitalInstanceDocument, "digitalDocument-" + record.getIdentifier(),
            // ".xml");
            throw new OaiAdapterException("Digital-document-registration data invalid:", ex);
        }

        // DIGITAL INSTANCE IMPORT
        Document digInstImportData = null;
        try {
            digInstImportData = XmlTools.getTransformedDocument(originalRecord.getDocument(), digInstImportTemplate);
            report("- OAI record successfuly transformed to digital-instance-import data - continuing.");
            // File tmpFile = saveToTempFile(digInstImportData, "digitalInstance-" +
            // originalRecord.getIdentifier(),
            // ".xml");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming record into digital-instance-import data:", ex);
        }
        try {
            XmlTools.validateByXsdAsString(digInstImportData, xsdProvider.getDigitalInstanceImportDataXsd());
            report("- Digital-instance-import data validation successful - continuing.");
            // File tmpFile = saveToTempFile(digitalInstanceDocument, "digitalInstance-" +
            // record.getIdentifier(),
            // ".xml");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-instance-import data invalid:", ex);
        }

        try {
            RecordResult recordResult = processSingleRecord(oaiIdentifier, digDocRegistrationData, digInstImportData);
            return recordResult;
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("Czidlo API error:", ex);
        }

    }

    public void run() {
        try {
            Document digDocRegistrationTemplate = getDigDocRegistrationTemplateDoc();
            Document digInstImportTemplate = getDigInstImportTemplateDoc();
            report("REPORT:");
            report("------------------------------");

            report(" OAI-PMH data provider");
            report(" -----------------");
            report("  Base url: " + getOaiBaseUrl());
            report("  Metadata prefix: " + getMetadataPrefix());
            report("  Set: " + (setSpec == null ? "none" : setSpec));
            report(" ");

            report(" CZIDLO API");
            report(" -----------------");
            report("  CZIDLO API base url: " + czidloConnector.getCzidloApiUrl());
            report("  Registrar code: " + getRegistrarCode());
            report("  Login: " + getCzidloApiConnector().getLogin());
            report("  Mode: " + getRegistrationMode());
            report(" ");

            report(" Transformations");
            report(" -----------------");
            if (metadataToDdRegistrationXsltFile != null) {
                report("  Metadata to DD-registration template: " + metadataToDdRegistrationXsltFile.getAbsolutePath());
            }
            report("  DD-registration schema location: " + getXsdProvider().getDigDocRegistrationDataXsdUrl().toString());
            if (metadataToDiImportXsltFile != null) {
                report("  Metadata to DI-import template: " + metadataToDiImportXsltFile.getAbsolutePath());
            }
            report("  DI-import schema location: " + getXsdProvider().getDigInstImportXsdUrl().toString());
            report(" ");

            report(" DI management");
            report(" -----------------");
            report("  Merge digital instances: " + mergeDigitalInstances);
            report("  Ignore difference in format: " + ignoreDifferenceInDiFormat);
            report("  Ignore difference in accessiblity: " + ignoreDifferenceInDiFormat);
            report("------------------------------");
            report(" ");

            try {
                if (!czidloConnector.checkRegistrarMode(getRegistrarCode(), getRegistrationMode())) {
                    report(" Mode " + getRegistrationMode() + " is not enabled for registrar " + getRegistrarCode());
                    logger.log(Level.SEVERE, "Mode {0} is not enabled for registrar {1}", new Object[]{getRegistrationMode(), getRegistrarCode()});
                    return;
                }
            } catch (CzidloConnectionException e) {
                report("Czidlo API not available: ", e);
                logger.log(Level.SEVERE, "Czidlo API not available: ", e);
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

            int ddRegisteredNow = 0;
            int ddRegisteredAlready = 0;
            int ddDeactivated = 0;

            int ddRegisteredNowDisImported = 0;
            int ddRegisteredNowDisUpdated = 0;
            int ddRegisteredNowDisUnchanged = 0;

            int ddRegisteredAlreadyDisImported = 0;
            int ddRegisteredAlreadyDisUpdated = 0;
            int ddRegisteredAlreadyDisUnchanged = 0;

            int all = 0;
            while (harvester.hasNext()) {
                if (limit > 0 && counter++ >= limit) {
                    break;
                }
                if (all % 100 == 0) {
                    logger.info(String.format("processed %d records", all));
                }
                try {
                    OriginalRecordFromOai record = harvester.getNext();
                    all++;
                    try {
                        RecordResult recordResult = processRecord(record, digDocRegistrationTemplate, digInstImportTemplate);
                        switch (recordResult.getDdStatus()) {
                            case IS_DEACTIVATED:
                                ddDeactivated++;
                                break;
                            case ALREADY_REGISTERED:
                                ddRegisteredAlready++;
                                switch (recordResult.getDiStatus()) {
                                    case IMPORTED:
                                        ddRegisteredAlreadyDisImported++;
                                        break;
                                    case UPDATED:
                                        ddRegisteredAlreadyDisUpdated++;
                                        break;
                                    case UNCHANGED:
                                        ddRegisteredAlreadyDisUnchanged++;
                                        break;
                                }
                                break;
                            case NOW_REGISTERED:
                                ddRegisteredNow++;
                                switch (recordResult.getDiStatus()) {
                                    case IMPORTED:
                                        ddRegisteredNowDisImported++;
                                        break;
                                    case UPDATED:
                                        ddRegisteredNowDisUpdated++;
                                        break;
                                    case UNCHANGED:
                                        ddRegisteredNowDisUnchanged++;
                                        break;
                                }
                        }

                        if (recordResult.getDdStatus() == DigitalDocumentStatus.IS_DEACTIVATED) {
                            report(String.format("* DD status: %s", recordResult.getDdStatus()));
                        } else {
                            Object ddStatussStr = recordResult.getDdStatus() == null ? null : recordResult.getDdStatus();
                            Object diStatusStr = recordResult.getDiStatus() == null ? "IGNORED" : recordResult.getDiStatus();
                            report(String.format("* DD status: %s, DI status: %s", ddStatussStr, diStatusStr));
                        }
                        if (recordResult.getUrnnbn() != null) {
                            report("* " + recordResult.getUrnnbn());
                        }

                        report("STATUS: OK");
                    } catch (OaiAdapterException ex) {
                        report(ex.getMessage());
                        if (ex.getCause() != null) {
                            report(ex.getCause().getMessage());
                        }
                        report("STATUS: NOT OK");
                    }
                } catch (OaiHarvesterException ex) {
                    report(String.format("OaiHarvester exception while getting document from %s: %s: ", ex.getUrl(), ex.getMessage()));
                    report("STATUS: NOT OK");
                }
            }
            report("=====================================================");
            report("ALL RECORDS: " + all);
            report("RECORDS WITH ERROR: " + (all - (ddRegisteredAlready + ddRegisteredNow + ddDeactivated)));
            report("RECORDS WITH URN:NBN DEACTIVATED: " + ddDeactivated);
            report("DDs REGISTERED ALREADY: " + ddRegisteredAlready);
            report("DDs REGISTERED NOW: " + ddRegisteredNow);
            if (ddRegisteredAlready != 0) {
                report("-----------------------------------------------------");
                report("DDs REGISTERED ALREADY: " + ddRegisteredAlready);
                report("	DIs IMPORTED NOW: " + ddRegisteredAlreadyDisImported);
                report("	DIs UPDATED NOW: " + ddRegisteredAlreadyDisUpdated);
                report("	DIs UNCHANGED: " + ddRegisteredAlreadyDisUnchanged);
            }
            if (ddRegisteredNow != 0) {
                report("-----------------------------------------------------");
                report("DDs REGISTERED NOW: " + ddRegisteredNow);
                report("	DIs IMPORTED NOW: " + ddRegisteredNowDisImported);
                report("	DIs UPDATED NOW: " + ddRegisteredNowDisUpdated);
                report("	DIs UNCHANGED: " + ddRegisteredNowDisUnchanged);
            }
            if (reportLogger != null) {
                reportLogger.close();
            }
        } catch (TemplateException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

    }

    private File saveToTempFile(Document document, String prefix, String suffix) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        XmlTools.saveDocumentToFile(document, tmpFile.getAbsolutePath());
        return tmpFile;
    }
}
