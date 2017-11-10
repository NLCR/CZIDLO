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

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.XsdProvider;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.utils.Credentials;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Martin Řehánek
 */
public class OaiAdapterJob extends AbstractJob {

    public static final String PARAM_REPORT_FILE = "report.txt";
    // oai
    public static final String PARAM_OAI_BASE_URL = "oaiBaseUrl";
    public static final String PARAM_OAI_METADATA_PREFIX = "oaiMetadataPrefix";
    public static final String PARAM_OAI_SET = "oaiSet";
    // transformations
    public static final String PARAM_DD_XSL_FILE = "ddXsl";
    public static final String PARAM_DI_XSL_FILE = "diXsl";
    public static final String PARAM_DD_XSD_URL = "ddXsdUrl";
    public static final String PARAM_DI_XSD_URL = "diXsdUrl";
    // czidlo api
    public static final String PARAM_CZIDLO_API_BASE_URL = "apiBaseUrl";
    public static final String PARAM_CZIDLO_REGISTRATION_MODE = "registrationMode";
    public static final String PARAM_CZIDLO_REGISTRAR_CODE = "registrarCode";
    public static final String PARAM_CZIDLO_API_LOGIN = "login";
    public static final String PARAM_CZIDLO_API_PASSWORD = "password";

    private OutputStream reportStream = null;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // System.setProperty("javax.xml.parsers.SAXParserFactory",
            // "org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.OAI_ADAPTER);
            logger.info("executing " + OaiAdapterJob.class.getName());
            OaiAdapter adapter = new OaiAdapter();
            //core
            CountryCode.initialize("CZ");
            // czidlo api
            String czidloApiBaseUrl = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_BASE_URL);
            logger.info("Czidlo API base url: " + czidloApiBaseUrl);
            Credentials czidloApicredentials = new Credentials(
                    (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_LOGIN),
                    (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_PASSWORD));
            // pozor, kvuli ignoreInvalidCertificate=false tohle nepojede na resolver-test a
            // resolver-test2, kde nejsou platne certifikaty
            adapter.setCzidloConnector(new CzidloApiConnector(czidloApiBaseUrl, czidloApicredentials, false));
            // TODO: 8.11.17 update according to changes in OaiAdapter
            //adapter.setRegistrationMode(UrnNbnRegistrationMode.valueOf((String) context.getMergedJobDataMap().get(PARAM_CZIDLO_REGISTRATION_MODE)));
            //logger.info("registration mode: " + adapter.getRegistrationMode().toString());
            adapter.setRegistrarCode((String) context.getMergedJobDataMap().get(PARAM_CZIDLO_REGISTRAR_CODE));
            logger.info("registrar code: " + adapter.getRegistrarCode());

            // oai provider
            adapter.setOaiBaseUrl((String) context.getMergedJobDataMap().get(PARAM_OAI_BASE_URL));
            logger.info("OAI base url: " + adapter.getOaiBaseUrl());
            adapter.setMetadataPrefix((String) context.getMergedJobDataMap().get(PARAM_OAI_METADATA_PREFIX));
            logger.info("OAI metadata prefix: " + adapter.getMetadataPrefix());
            adapter.setSetSpec((String) context.getMergedJobDataMap().get(PARAM_OAI_SET));
            logger.info("OAI set: " + (adapter.getSetSpec() == null ? "NOT DEFINED" : adapter.getSetSpec()));

            // oai response -> dd registration XSLT
            String ddRegistrationXslt = (String) context.getMergedJobDataMap().get(PARAM_DD_XSL_FILE);
            logger.info("XSL template to transform oai response to DD registration data: " + ddRegistrationXslt);
            adapter.setMetadataToDdRegistrationXslt(null, XmlTools.loadXmlFromFile(ddRegistrationXslt));

            // oai response -> di import XSLT
            String diImportXslt = (String) context.getMergedJobDataMap().get(PARAM_DI_XSL_FILE);
            logger.info("XSL template to transform oai response to DI import data: " + diImportXslt);
            adapter.setMetadataToDiImportXslt(null, XmlTools.loadXmlFromFile(diImportXslt));

            // XSDs
            String ddRegistrationXsdUrl = (String) context.getMergedJobDataMap().get(PARAM_DD_XSD_URL);
            logger.info("XSD for validation of DD registration data: " + ddRegistrationXsdUrl);
            String diImportXsdUrl = (String) context.getMergedJobDataMap().get(PARAM_DI_XSD_URL);
            logger.info("XSD for validation of DI import data: " + diImportXsdUrl);
            adapter.setXsdProvider(new XsdProvider(new URL(ddRegistrationXsdUrl), new URL(diImportXsdUrl)));

            //DI import other stuff
            // TODO: 28.10.17 incorporate new paramethers
            /*adapter.setMergeDigitalInstances();
            adapter.setIgnoreDifferenceInDiAccessibility();
            adapter.setIgnoreDifferenceInDiFormat();*/

            // report
            reportStream = fileToOutputStream(createWriteableProcessFile(PARAM_REPORT_FILE));
            adapter.setOutputStream(reportStream);
            logger.info("running oai adapter");
            adapter.run();
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
                logger.info("process killed");
            } else {
                context.setResult(ProcessState.FINISHED);
                logger.info("oai adapter finished, see report");
            }
        } catch (Throwable ex) {
            logger.error("OAI Adapter process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    @Override
    void close() {
        if (reportStream != null) {
            try {
                reportStream.close();
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
    }
}
