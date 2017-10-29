/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.oaiadapter.RecordResult.DigitalDocumentStatus;
import cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloConnectionException;
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
    //private int limit = -1;
    private int limit = 13;//dev only
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

    private Document buildDigDocRegistrationTemplateDoc() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(metadataToDdRegistrationXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building Digital-document-registration template: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building Digital-document-registration template: " + ex.getMessage());
        }
    }

    private Document buildDigInstImportTemplateDoc() throws TemplateException {
        try {
            return XmlTools.getTemplateDocumentFromString(metadataToDiImportXslt);
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
            SingleRecordProcessor recordProcessor = new SingleRecordProcessor(this, registrarCode, registrationMode, czidloConnector, xsdProvider, mergeDigitalInstances, ignoreDifferenceInDiAccessibility, ignoreDifferenceInDiFormat);
            Document digDocRegistrationTemplate = buildDigDocRegistrationTemplateDoc();
            Document digInstImportTemplate = buildDigInstImportTemplateDoc();
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
                        RecordResult recordResult = recordProcessor.processRecord(record, digDocRegistrationTemplate, digInstImportTemplate);
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


}
