/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.utils.Credentials;
import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.oaiadapter.RecordResult.DigitalDocumentStatus;
import cz.nkp.urnnbn.oaiadapter.cli.XslTemplate;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class OaiAdapter {

    public static final String REGISTAR_SCOPE_ID_TYPE = "OAI_Adapter";
    private static final Logger logger = Logger.getLogger(OaiAdapter.class.getName());
    //registrar code
    private final String registrarCode;
    // OAI
    private final String oaiBaseUrl;
    private final String oaiMetadataPrefix;
    private final String oaiSetSpec;
    // CZIDLO API
    private final String czidloApiBaseUrl;
    private final String czidloApiLogin;
    private final String czidloApiPassword;
    private final boolean czidloApiIgnoreInvalidCertificate;
    // XSLT
    private final XslTemplate metadataToDdRegistrationXslt;
    private final XslTemplate metadataToDiImportXslt;
    //XSD
    private final URL ddRegistrationDataXsdUrl;
    private final URL diImportDataXsdUrl;
    // DD
    private final boolean registerDDsWithUrn;
    private final boolean registerDDsWithoutUrn;
    // DI
    private final boolean mergeDigitalInstances;
    private final boolean ignoreDifferenceInDiAccessibility;
    private final boolean ignoreDifferenceInDiFormat;
    //report
    private final ReportLogger reportLogger;

    //DEV
    private int devRecordLimit = -1;
    //private int devRecordLimit = 3;//dev only

    public OaiAdapter(String registrarCode,
                      String oaiBaseUrl, String oaiMetadataPrefix, String oaiSetSpec, String czidloApiBaseUrl,
                      String czidloApiLogin, String czidloApiPassword, boolean czidloApiIgnoreInvalidCertificate,
                      XslTemplate metadataToDdRegistrationXslt, XslTemplate metadataToDiImportXslt,
                      URL ddRegistrationDataXsdUrl, URL diImportDataXsdUrl,
                      boolean registerDDsWithUrn, boolean registerDDsWithoutUrn,
                      boolean mergeDigitalInstances, boolean ignoreDifferenceInDiAccessibility, boolean ignoreDifferenceInDiFormat,
                      ReportLogger reportLogger
    ) {
        this.registrarCode = registrarCode;
        this.oaiBaseUrl = oaiBaseUrl;
        this.oaiMetadataPrefix = oaiMetadataPrefix;
        this.oaiSetSpec = oaiSetSpec;
        this.czidloApiBaseUrl = czidloApiBaseUrl;
        this.czidloApiLogin = czidloApiLogin;
        this.czidloApiPassword = czidloApiPassword;
        this.czidloApiIgnoreInvalidCertificate = czidloApiIgnoreInvalidCertificate;
        this.metadataToDdRegistrationXslt = metadataToDdRegistrationXslt;
        this.metadataToDiImportXslt = metadataToDiImportXslt;
        this.ddRegistrationDataXsdUrl = ddRegistrationDataXsdUrl;
        this.diImportDataXsdUrl = diImportDataXsdUrl;
        this.registerDDsWithUrn = registerDDsWithUrn;
        this.registerDDsWithoutUrn = registerDDsWithoutUrn;
        this.mergeDigitalInstances = mergeDigitalInstances;
        this.ignoreDifferenceInDiAccessibility = ignoreDifferenceInDiAccessibility;
        this.ignoreDifferenceInDiFormat = ignoreDifferenceInDiFormat;
        this.reportLogger = reportLogger;
    }

    private void report(String message) {
        reportLogger.report(message);
    }

    private void report(String message, Throwable e) {
        reportLogger.report(message, e);
    }

    public void run() {
        try {
            report("Parameters");
            report("==============================");
            reportParams();

            report("Initialization");
            report("==============================");
            Counters counters = new Counters();
            OaiHarvester harvester = new OaiHarvester(oaiBaseUrl, oaiMetadataPrefix, oaiSetSpec, reportLogger);
            report("- OaiHarvester initialized");
            CzidloApiConnector czidloApiConnector = new CzidloApiConnector(czidloApiBaseUrl, new Credentials(czidloApiLogin, czidloApiPassword), true, czidloApiIgnoreInvalidCertificate);
            Document digDocRegistrationXslt = buildXsltDoc(metadataToDdRegistrationXslt, "Digital-document-registration");
            Document digInstImportXslt = buildXsltDoc(metadataToDiImportXslt, "Digital-instance-import");

            XsdProvider xsdProvider = new XsdProvider(ddRegistrationDataXsdUrl, diImportDataXsdUrl);
            SingleRecordProcessor recordProcessor = new SingleRecordProcessor(reportLogger, registrarCode, czidloApiConnector, digDocRegistrationXslt, digInstImportXslt, xsdProvider, registerDDsWithUrn, registerDDsWithoutUrn, mergeDigitalInstances, ignoreDifferenceInDiAccessibility, ignoreDifferenceInDiFormat);
            // TODO: 7.11.17 other tests of parameters like existence of registrar, digital library (id), access rights, API availability etc.
            report(" ");

            report("Records");
            report("==============================");
            while (harvester.existsNextIdentifier()) {
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
        } catch (OaiHarvesterException ex) {
            report("OaiHarvester initialization failed: " + ex.getMessage() + ", url: " + ex.getUrl());
            logger.log(Level.SEVERE, "OaiHarvester initialization failed: {0}, url: {1}", new Object[]{ex.getMessage(), ex.getUrl()});
            return;
        } catch (IOException e) {
            report("Initialization error: IOException: " + e.getMessage());
            logger.log(Level.SEVERE, "Initialization error", e);
        } catch (TemplateException e) {
            report("Initialization error: : TemplateException: " + e.getMessage());
            logger.log(Level.SEVERE, "Template initialization error", e);
        } finally {
            if (reportLogger != null) {
                reportLogger.close();
            }
        }
    }

    private Document buildXsltDoc(XslTemplate template, String type) throws TemplateException {
        try {
            return XmlTools.parseDocumentFromString(template.getXml());
        } catch (XSLException ex) {
            throw new TemplateException(String.format("XSLException occurred during building %s xslt %s: %s ", type, template.getDescription(), ex.getMessage()));
        } catch (ParsingException ex) {
            throw new TemplateException(String.format("ParsingException occurred during building %s xslt %s: %s ", type, template.getDescription(), ex.getMessage()));
        } catch (IOException ex) {
            throw new TemplateException(String.format("IOException occurred during building %s xslt %s: %s ", type, template.getDescription(), ex.getMessage()));
        }
    }

    private void reportParams() {
        report(" Core");
        report(" -----------------");
        report("  Registrar code: " + registrarCode);
        report(" ");

        report(" OAI-PMH data provider");
        report(" -----------------");
        report("  Base url: " + oaiBaseUrl);
        report("  Metadata prefix: " + oaiMetadataPrefix);
        report("  Set: " + (oaiSetSpec == null ? "none" : oaiSetSpec));
        report(" ");

        report(" CZIDLO API");
        report(" -----------------");
        report("  CZIDLO API base url: " + czidloApiBaseUrl);
        report("  Login: " + czidloApiLogin);
        report("  Ignore invalid certificate: " + czidloApiIgnoreInvalidCertificate);
        report(" ");

        report(" Transformations");
        report(" -----------------");
        if (metadataToDdRegistrationXslt != null && metadataToDdRegistrationXslt.getId() != null) {
            report("  Metadata to DD-registration template: " + metadataToDdRegistrationXslt.getId());
        }
        if (metadataToDdRegistrationXslt != null && metadataToDdRegistrationXslt.getFile() != null) {
            report("  Metadata to DD-registration template file: " + metadataToDdRegistrationXslt.getFile().getAbsolutePath());
        }
        report("  DD-registration schema location: " + ddRegistrationDataXsdUrl.toString());

        if (metadataToDiImportXslt != null && metadataToDiImportXslt.getId() != null) {
            report("  Metadata to DI-import template: " + metadataToDiImportXslt.getId());
        }
        if (metadataToDiImportXslt != null && metadataToDiImportXslt.getFile() != null) {
            report("  Metadata to DI-import template file: " + metadataToDiImportXslt.getFile().getAbsolutePath());
        }
        report("  DI-import schema location: " + diImportDataXsdUrl.toString());
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
        if (devRecordLimit > 0 && counters.all >= devRecordLimit) {
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
            OaiRecord record = harvester.getNextRecord();
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

    public String getRegistrarCode() {
        return registrarCode;
    }

    public String getOaiBaseUrl() {
        return oaiBaseUrl;
    }

    public String getOaiMetadataPrefix() {
        return oaiMetadataPrefix;
    }

    public String getOaiSetSpec() {
        return oaiSetSpec;
    }

    public String getCzidloApiBaseUrl() {
        return czidloApiBaseUrl;
    }

    public String getCzidloApiLogin() {
        return czidloApiLogin;
    }

    public String getCzidloApiPassword() {
        return czidloApiPassword;
    }

    public boolean isCzidloApiIgnoreInvalidCertificate() {
        return czidloApiIgnoreInvalidCertificate;
    }

    public String getMetadataToDdRegistrationXslt() {
        return metadataToDdRegistrationXslt.getXml();
    }

    public File getMetadataToDdRegistrationXsltFile() {
        return metadataToDdRegistrationXslt.getFile();
    }

    public String getMetadataToDiImportXslt() {
        return metadataToDiImportXslt.getXml();
    }

    public File getMetadataToDiImportXsltFile() {
        return metadataToDiImportXslt.getFile();
    }

    public URL getDdRegistrationDataXsdUrl() {
        return ddRegistrationDataXsdUrl;
    }

    public URL getDiImportDataXsdUrl() {
        return diImportDataXsdUrl;
    }

    public boolean isRegisterDDsWithUrn() {
        return registerDDsWithUrn;
    }

    public boolean isRegisterDDsWithoutUrn() {
        return registerDDsWithoutUrn;
    }

    public boolean isMergeDigitalInstances() {
        return mergeDigitalInstances;
    }

    public boolean isIgnoreDifferenceInDiAccessibility() {
        return ignoreDifferenceInDiAccessibility;
    }

    public boolean isIgnoreDifferenceInDiFormat() {
        return ignoreDifferenceInDiFormat;
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
