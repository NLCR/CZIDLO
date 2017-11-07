/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.RecordResult.DigitalDocumentStatus;
import cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private CzidloApiConnector czidloConnector;
    // XSLT
    private String metadataToDdRegistrationXslt;
    private File metadataToDdRegistrationXsltFile;
    private String metadataToDiImportXslt;
    private File metadataToDiImportXsltFile;
    // XSD
    private XsdProvider xsdProvider;
    // DD
    private boolean registerDDsWithUrn = DefinedProperties.DD_REGISTRATION_REGISTER_DDS_WITH_URN_DEFAULT;
    private boolean registerDDsWithoutUrn = DefinedProperties.DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN_DEFAULT;

    // DI
    private boolean mergeDigitalInstances = DefinedProperties.DI_IMPORT_MERGE_DIS_DEFAULT;
    private boolean ignoreDifferenceInDiAccessibility = DefinedProperties.DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT;
    private boolean ignoreDifferenceInDiFormat = DefinedProperties.DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT;
    // OTHER
    private int limit = -1;
    //private int limit = 3;//dev only

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

    public void setRegisterDDsWithUrn(boolean registerDDsWithUrn) {
        this.registerDDsWithUrn = registerDDsWithUrn;
    }

    public void setRegisterDDsWithoutUrn(boolean registerDDsWithoutUrn) {
        this.registerDDsWithoutUrn = registerDDsWithoutUrn;
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

    private Document buildDigDocRegistrationXsltDoc() throws TemplateException {
        try {
            return XmlTools.parseDocumentFromString(metadataToDdRegistrationXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building Digital-document-registration template: " + ex.getMessage());
        }
    }

    private Document buildDigInstImportXsltDoc() throws TemplateException {
        try {
            return XmlTools.parseDocumentFromString(metadataToDiImportXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building Digital-instance-import template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building Digital-instance-import template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building Digital-instance-import template: " + ex.getMessage());
        }
    }

    public void report(String message) {
        if (reportLogger != null) {
            reportLogger.report(message);
        }
    }

    public void report(String message, Throwable e) {
        if (reportLogger != null) {
            reportLogger.report(message, e);
        }
    }

    public void run() {
        try {
            Document digDocRegistrationXslt = buildDigDocRegistrationXsltDoc();
            Document digInstImportXslt = buildDigInstImportXsltDoc();
            SingleRecordProcessor recordProcessor = new SingleRecordProcessor(this, registrarCode, czidloConnector, digDocRegistrationXslt, digInstImportXslt, xsdProvider, registerDDsWithUrn, registerDDsWithoutUrn, mergeDigitalInstances, ignoreDifferenceInDiAccessibility, ignoreDifferenceInDiFormat);
            report("Parameters");
            report("==============================");
            reportParams();

            report("Initialization");
            report("==============================");
            Counters counters = new Counters();
            OaiHarvester harvester = null;
            try {
                harvester = new OaiHarvester(getOaiBaseUrl(), getMetadataPrefix(), getSetSpec());
                report("- OaiHarvester initialized");
                // TODO: 7.11.17 other tests of parameters like existence of registrar, digital library (id), access rights, API availability etc.
            } catch (OaiHarvesterException ex) {
                report("OaiHarvester initialization failed. " + ex.getMessage() + ", url: " + ex.getMessage());
                logger.log(Level.SEVERE, "OaiHarvester initialization failed. {0}, url: {1}", new Object[]{ex.getMessage(), ex.getMessage()});
                return;
            }
            report(" ");

            report("Records");
            report("==============================");
            while (harvester.hasNext()) {
                boolean quitNow = processRecord(counters, harvester, recordProcessor);
                if (quitNow) {
                    break;
                }
            }
            logger.info(String.format("processed %d records", counters.all));
            report(" ");

            report("Summary");
            report("=====================================================");
            reportSummary(counters);

            if (reportLogger != null) {
                reportLogger.close();
            }
        } catch (TemplateException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    private void reportParams() {
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

        report(" DD management");
        report(" -----------------");
        report("  Register digital documents with URN:NBN: " + registerDDsWithUrn);
        report("  Register digital documents without URN:NBN: " + registerDDsWithoutUrn);
        report(" ");

        report(" DI management");
        report(" -----------------");
        report("  Merge digital instances: " + mergeDigitalInstances);
        report("  Ignore difference in format: " + ignoreDifferenceInDiFormat);
        report("  Ignore difference in accessiblity: " + ignoreDifferenceInDiFormat);
        report(" ");
    }

    private boolean processRecord(Counters counters, OaiHarvester harvester, SingleRecordProcessor recordProcessor) {
        if (limit > 0 && counters.all >= limit) {
            return true;
        }
        if (counters.all != 0 && counters.all % 100 == 0) {
            logger.info(String.format("processed %d records", counters.all));
        }
        try {
            if (counters.all > 0) {
                report("------------------------------------------------------");
            }
            counters.all++;
            OaiRecord record = harvester.getNext();
            RecordResult recordResult = recordProcessor.processRecord(record);
            switch (recordResult.getDdStatus()) {
                case IS_DEACTIVATED:
                    counters.ddDeactivated++;
                    break;
                case REGISTERED_ALREADY:
                    counters.ddRegisteredAlready++;
                    switch (recordResult.getDiStatus()) {
                        case IMPORTED:
                            counters.ddRegisteredAlreadyDisImported++;
                            break;
                        case UPDATED:
                            counters.ddRegisteredAlreadyDisUpdated++;
                            break;
                        case UNCHANGED:
                            counters.ddRegisteredAlreadyDisUnchanged++;
                            break;
                    }
                    break;
                case REGISTERED_NOW:
                    counters.ddRegisteredNow++;
                    switch (recordResult.getDiStatus()) {
                        case IMPORTED:
                            counters.ddRegisteredNowDisImported++;
                            break;
                        case UPDATED:
                            counters.ddRegisteredNowDisUpdated++;
                            break;
                        case UNCHANGED:
                            counters.ddRegisteredNowDisUnchanged++;
                            break;
                    }
                case NOT_REGISTERED:
                    counters.ddNotRegistered++;
                    break;
            }

            report("Results");
            if (recordResult.getUrnnbn() != null) {
                report("* " + recordResult.getUrnnbn());
            }
            if (recordResult.getDdStatus() == DigitalDocumentStatus.IS_DEACTIVATED) {
                report(String.format("* DD status: %s", recordResult.getDdStatus()));
            } else {
                Object ddStatusStr = recordResult.getDdStatus() == null ? null : recordResult.getDdStatus();
                Object diStatusStr = recordResult.getDiStatus() == null ? "IGNORED" : recordResult.getDiStatus();
                report("* DD status: " + ddStatusStr);
                report("* DI status: " + diStatusStr);
            }
            report("Status: OK");
        } catch (SingleRecordProcessingException ex) {
            counters.errors++;
            report(ex.getMessage());
            if (ex.getCause() != null) {
                String clauseMessage = ex.getCause().getMessage();
                if (clauseMessage != null && !clauseMessage.trim().isEmpty()) {
                    report(clauseMessage.trim());
                }
            }
            report("Status: ERROR");
        } catch (OaiHarvesterException ex) {
            counters.errors++;
            report(String.format("OaiHarvester exception while getting document from %s: %s: ", ex.getUrl(), ex.getMessage()));
            report("Status: ERROR");
        }
        return false;
    }

    private void reportSummary(Counters counters) {
        report("RECORDS: " + counters.all);
        report("ERRORS: " + counters.errors);

        report("DDs REGISTERED ALREADY: " + counters.ddRegisteredAlready);
        report("DDs REGISTERED NOW: " + counters.ddRegisteredNow);
        report("DDs DEACTIVATED: " + counters.ddDeactivated);
        report("DDs NOT REGISTERED: " + counters.ddNotRegistered);

        if (counters.ddRegisteredAlready != 0) {
            report("-----------------------------------------------------");
            report("DDs REGISTERED ALREADY: " + counters.ddRegisteredAlready);
            report("	DIs IMPORTED NOW: " + counters.ddRegisteredAlreadyDisImported);
            report("	DIs UPDATED NOW: " + counters.ddRegisteredAlreadyDisUpdated);
            report("	DIs UNCHANGED: " + counters.ddRegisteredAlreadyDisUnchanged);
        }
        if (counters.ddRegisteredNow != 0) {
            report("-----------------------------------------------------");
            report("DDs REGISTERED NOW: " + counters.ddRegisteredNow);
            report("	DIs IMPORTED NOW: " + counters.ddRegisteredNowDisImported);
            report("	DIs UPDATED NOW: " + counters.ddRegisteredNowDisUpdated);
            report("	DIs UNCHANGED: " + counters.ddRegisteredNowDisUnchanged);
        }
    }


    public static class Counters {
        int all = 0;
        int errors = 0;

        int ddRegisteredNow = 0;
        int ddRegisteredAlready = 0;
        int ddDeactivated = 0;
        int ddNotRegistered = 0;

        int ddRegisteredNowDisImported = 0;
        int ddRegisteredNowDisUpdated = 0;
        int ddRegisteredNowDisUnchanged = 0;

        int ddRegisteredAlreadyDisImported = 0;
        int ddRegisteredAlreadyDisUpdated = 0;
        int ddRegisteredAlreadyDisUnchanged = 0;

    }

}
