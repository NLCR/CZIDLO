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

import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.XsdProvider;
import cz.nkp.urnnbn.oaiadapter.resolver.RegistrationMode;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnector;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import java.io.IOException;
import java.io.OutputStream;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Martin Řehánek
 */
public class OaiAdapterJob extends AbstractJob {
    
    public static final String REPORT_FILE_NAME = "report.txt";
    public static final String PARAM_RESOLVER_API_URL = "apiUrl";
    public static final String PARAM_RESOLVER_REGISTRATION_MODE = "registrationMode";
    public static final String PARAM_RESOLVER_REGISTRAR_CODE = "registrarCode";
    public static final String PARAM_RESOLVER_LOGIN = "login";
    public static final String PARAM_RESOLVER_PASS = "pass";
    public static final String PARAM_OAI_BASE_URL = "oaiBaseUrl";
    public static final String PARAM_OAI_METADATA_PREFIX = "oaiMetadataPrefix";
    public static final String PARAM_OAI_SET = "oaiSet";
    public static final String PARAM_DD_XSL_FILE = "ddXsl";
    public static final String PARAM_DI_XSL_FILE = "diXsl";
    public static final String PARAM_DD_XSD_URL = "ddXsdUrl";
    public static final String PARAM_DI_XSD_URL = "diXsdUrl";
    public static final String PARAM_REPORT_FILE = "report";
    private OutputStream reportStream = null;
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.OAI_ADAPTER);
            logger.info("executing " + OaiAdapterJob.class.getName());
            OaiAdapter adapter = new OaiAdapter();

            //resolver
            String resolverApiUrl = (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_API_URL);
            logger.info("Resolver API url: " + resolverApiUrl);
            adapter.setResolverConnector(new ResolverConnector(resolverApiUrl,
                    (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_LOGIN),
                    (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_PASS)));
            adapter.setRegistrationMode(RegistrationMode.valueOf((String) context.getMergedJobDataMap().get(PARAM_RESOLVER_REGISTRATION_MODE)));
            logger.info("registration mode: " + adapter.getRegistrationMode().toString());
            adapter.setRegistrarCode((String) context.getMergedJobDataMap().get(PARAM_RESOLVER_REGISTRAR_CODE));
            logger.info("registrar code: " + adapter.getRegistrarCode());

            //oai provider
            adapter.setOaiBaseUrl((String) context.getMergedJobDataMap().get(PARAM_OAI_BASE_URL));
            logger.info("OAI base url: " + adapter.getOaiBaseUrl());
            adapter.setMetadataPrefix((String) context.getMergedJobDataMap().get(PARAM_OAI_METADATA_PREFIX));
            logger.info("OAI metadata prefix: " + adapter.getMetadataPrefix());
            adapter.setSetSpec((String) context.getMergedJobDataMap().get(PARAM_OAI_SET));
            logger.info("OAI set: " + adapter.getSetSpec() == null ? "NOT DEFINED" : adapter.getSetSpec());

            //oai response -> dd registration XSLT
            String ddRegistrationXslt = (String) context.getMergedJobDataMap().get(PARAM_DD_XSL_FILE);
            logger.info("XSL template to transform oai response to DD registration data: " + ddRegistrationXslt);
            adapter.setMetadataToImportTemplate(XmlTools.loadXmlFromFile(ddRegistrationXslt));

            //oai response -> di import XSLT
            String diImportXslt = (String) context.getMergedJobDataMap().get(PARAM_DI_XSL_FILE);
            logger.info("XSL template to transform oai response to DI import data: " + diImportXslt);
            adapter.setMetadataToDigitalInstanceTemplate(XmlTools.loadXmlFromFile(diImportXslt));

            //XSDs
            String ddRegistrationXsdUrl = (String) context.getMergedJobDataMap().get(PARAM_DD_XSD_URL);
            logger.info("XSD for validation of DD registration data: " + ddRegistrationXsdUrl);
            String diImportXsdUrl = (String) context.getMergedJobDataMap().get(PARAM_DI_XSD_URL);
            logger.info("XSD for validation of DI import data: " + diImportXsdUrl);
            adapter.setXsdProvider(new XsdProvider(ddRegistrationXsdUrl, diImportXsdUrl));

            //report
            reportStream = fileToOutputStream(createWriteableProcessFile(REPORT_FILE_NAME));
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