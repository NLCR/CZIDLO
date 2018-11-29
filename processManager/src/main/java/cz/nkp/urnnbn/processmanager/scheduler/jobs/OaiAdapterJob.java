/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.ReportLogger;
import cz.nkp.urnnbn.oaiadapter.cli.XslTemplate;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @author Martin Řehánek
 */
public class OaiAdapterJob extends AbstractJob {

    //core
    public static final String PARAM_CZIDLO_REGISTRAR_CODE = "registrarCode";
    public static final String PARAM_REPORT_FILE = "report.txt";
    //oai
    public static final String PARAM_OAI_BASE_URL = "oaiBaseUrl";
    public static final String PARAM_OAI_METADATA_PREFIX = "oaiMetadataPrefix";
    public static final String PARAM_OAI_SET = "oaiSet";
    //czidlo api
    public static final String PARAM_CZIDLO_API_BASE_URL = "czidloApiBaseUrl";
    public static final String PARAM_CZIDLO_API_LOGIN = "czidloApiLogin";
    public static final String PARAM_CZIDLO_API_PASSWORD = "czidloApiPassword";
    //xslt
    public static final String PARAM_DD_REGISTRATION_XSL_ID = "ddRegistrationXslId";
    public static final String PARAM_DD_REGISTRATION_XSL_FILE = "ddRegistrationXslFile";
    public static final String PARAM_DI_IMPORT_XSL_ID = "diImportXslId";
    public static final String PARAM_DI_IMPORT_XSL_FILE = "diImportXslFile";
    //xsd
    public static final String PARAM_DD_REGISTRATION_XSD_URL = "ddRegistrationXsdUrl";
    public static final String PARAM_DI_IMPORT_XSD_URL = "diImportXsdUrl";
    // dd registration
    public static final String PARAM_DD_REGISTRATION_REGISTER_DDS_WITH_URN = "ddRegistration.registerDigitalDocumentsWithUrnNbn";
    public static final String PARAM_DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN = "ddRegistration.registerDigitalDocumentsWithoutUrnNbn";
    // di import
    public static final String PARAM_DI_IMPORT_MERGE_DIS = "diImport.mergeDigitalInstances";
    public static final String PARAM_DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY = "diImport.ignoreDifferenceInAccessibility";
    public static final String PARAM_DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT = "diImport.ignoreDifferenceInFormat";

    private ReportLogger reportLogger;
    private OaiAdapter oaiAdapter;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        super.interrupt();
        if (oaiAdapter != null) {
            oaiAdapter.stop();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.OAI_ADAPTER);
            logger.info("Executing " + OaiAdapterJob.class.getSimpleName());
            //core
            CountryCode.initialize("CZ");
            String registrarCode = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_REGISTRAR_CODE);
            logger.info("Registrar code: " + registrarCode);
            // oai provider
            String oaiBaseUrl = (String) context.getMergedJobDataMap().get(PARAM_OAI_BASE_URL);
            logger.info("OAI base url: " + oaiBaseUrl);
            String oaiMetadataPrefix = (String) context.getMergedJobDataMap().get(PARAM_OAI_METADATA_PREFIX);
            logger.info("OAI metadata prefix: " + oaiMetadataPrefix);
            String oaiSet = (String) context.getMergedJobDataMap().get(PARAM_OAI_SET);
            logger.info("OAI set: " + (oaiSet == null ? "NOT DEFINED" : oaiSet));
            // czidlo api
            String czidloApiBaseUrl = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_BASE_URL);
            logger.info("Czidlo API base url: " + czidloApiBaseUrl);
            String czidloApiLogin = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_LOGIN);
            logger.info("Czidlo API login: " + czidloApiLogin);
            String czidloApiPassword = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_PASSWORD);
            // oai response -> dd registration XSLT
            String ddRegistrationXsltId = (String) context.getMergedJobDataMap().get(PARAM_DD_REGISTRATION_XSL_ID);
            File ddRegistrationXsltFile = new File((String) context.getMergedJobDataMap().get(PARAM_DD_REGISTRATION_XSL_FILE));
            logger.info("XSL template to transform OAI record to DD registration data: " + ddRegistrationXsltId);
            // oai response -> di import XSLT
            String diImportXsltId = (String) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_XSL_ID);
            File diImportXsltXmlFile = new File((String) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_XSL_FILE));
            logger.info("XSL template to transform OAI record to DI import data: " + diImportXsltId);
            // XSDs
            URL ddRegistrationXsdUrl = new URL((String) context.getMergedJobDataMap().get(PARAM_DD_REGISTRATION_XSD_URL));
            logger.info("XSD for validation of DD registration data: " + ddRegistrationXsdUrl);
            URL diImportXsdUrl = new URL((String) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_XSD_URL));
            logger.info("XSD for validation of DI import data: " + diImportXsdUrl);
            //dd
            Boolean registerDDsWithUrn = (Boolean) context.getMergedJobDataMap().get(PARAM_DD_REGISTRATION_REGISTER_DDS_WITH_URN);
            logger.info("Register digital documents with urn:nbn: " + registerDDsWithUrn);
            Boolean registerDDsWithoutUrn = (Boolean) context.getMergedJobDataMap().get(PARAM_DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN);
            logger.info("Register digital documents without urn:nbn: " + registerDDsWithoutUrn);
            //di
            Boolean mergeDigitalInstances = (Boolean) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_MERGE_DIS);
            logger.info("Merge digital instances: " + mergeDigitalInstances);
            Boolean ignoreDifferenceInDiAccessibility = (Boolean) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY);
            logger.info("Ignore difference in DI accessibility: " + ignoreDifferenceInDiAccessibility);
            Boolean ignoreDifferenceInDiFormat = (Boolean) context.getMergedJobDataMap().get(PARAM_DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT);
            logger.info("Ignore difference in DI format: " + ignoreDifferenceInDiFormat);

            //prepare report logger
            File reportFile = createWriteableProcessFile(PARAM_REPORT_FILE);
            reportLogger = buildReportLogger(reportFile);
            //run
            logger.info("Running OAI Adapter");
            oaiAdapter = new OaiAdapter(registrarCode,
                    oaiBaseUrl, oaiMetadataPrefix, oaiSet,
                    czidloApiBaseUrl, czidloApiLogin, czidloApiPassword, false,
                    new XslTemplate(ddRegistrationXsltId, XmlTools.loadXmlFromFile(ddRegistrationXsltFile.getAbsolutePath()), ddRegistrationXsltFile),
                    new XslTemplate(diImportXsltId, XmlTools.loadXmlFromFile(diImportXsltXmlFile.getAbsolutePath()), diImportXsltXmlFile),
                    ddRegistrationXsdUrl, diImportXsdUrl,
                    registerDDsWithUrn, registerDDsWithoutUrn,
                    mergeDigitalInstances, ignoreDifferenceInDiAccessibility, ignoreDifferenceInDiFormat,
                    reportLogger
            );
            oaiAdapter.run();
            if (interrupted) {
                logger.info("Process killed");
                context.setResult(ProcessState.KILLED);
            } else {
                logger.info("Process finished, see report");
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            logger.error("Process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    private ReportLogger buildReportLogger(File reportFile) throws Exception {
        try {
            return new ReportLogger(new FileOutputStream(reportFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }

    @Override
    void close() {
        if (reportLogger != null) {
            reportLogger.close();
        }
    }
}
