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
    public static final String PARAM_REPORT_FILE = "report";
    private OutputStream reportStream = null;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {

            //System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            //System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.parsers.SAXParser");
            //TODO:FUCKing xerces
            System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");

            init(context.getMergedJobDataMap(), ProcessType.OAI_ADAPTER);
            logger.info("executing " + OaiAdapterJob.class.getName());
            OaiAdapter adapter = new OaiAdapter();
            //resolver
            adapter.setResolverConnector(new ResolverConnector(
                    (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_API_URL),
                    (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_LOGIN),
                    (String) context.getMergedJobDataMap().get(PARAM_RESOLVER_PASS)));
            adapter.setRegistrationMode(RegistrationMode.valueOf((String) context.getMergedJobDataMap().get(PARAM_RESOLVER_REGISTRATION_MODE)));
            adapter.setRegistrarCode((String) context.getMergedJobDataMap().get(PARAM_RESOLVER_REGISTRAR_CODE));
            //oai provider
            adapter.setOaiBaseUrl((String) context.getMergedJobDataMap().get(PARAM_OAI_BASE_URL));
            adapter.setMetadataPrefix((String) context.getMergedJobDataMap().get(PARAM_OAI_METADATA_PREFIX));
            adapter.setSetSpec((String) context.getMergedJobDataMap().get(PARAM_OAI_SET));
            //xsl
            adapter.setMetadataToDigitalInstanceTemplate(XmlTools.loadXmlFromFile((String) context.getMergedJobDataMap().get(PARAM_DD_XSL_FILE)));
            adapter.setMetadataToImportTemplate(XmlTools.loadXmlFromFile((String) context.getMergedJobDataMap().get(PARAM_DI_XSL_FILE)));
            //report
            reportStream = fileToOutputStream(createWriteableProcessFile(REPORT_FILE_NAME));
            adapter.setOutputStream(reportStream);
            //TODO: check when Adapter throws Exception
            //TODO: 
            adapter.run();
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
            } else {
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            logger.error(ex.getMessage());
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
