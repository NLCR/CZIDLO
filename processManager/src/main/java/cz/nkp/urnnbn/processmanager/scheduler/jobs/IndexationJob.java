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
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.solr_indexer.DataProvider;
import cz.nkp.urnnbn.solr_indexer.ProgressListener;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Martin Řehánek
 */
public class IndexationJob extends AbstractJob {

    //report file
    public static final String PARAM_REPORT_FILE = "report.txt";
    //czidlo api
    public static final String PARAM_CZIDLO_API_BASE_URL = "czidloApiBaseUrl";
    //solr
    public static final String PARAM_SOLR_BASE_URL = "solrBaseUrl";
    public static final String PARAM_SOLR_COLLECTION = "solrCollection";
    public static final String PARAM_SOLR_USE_HTTPS = "solrUseHttps";
    public static final String PARAM_SOLR_LOGIN = "solrLogin";
    public static final String PARAM_SOLR_PASSWORD = "solrPassword";
    //xslt
    public static final String PARAM_XSL_FILE = "xslFile";
    //date range
    public static final String PARAM_MODIFICATION_DATE_FROM = "mod_date_from";
    public static final String PARAM_MODIFICATION_DATE_TO = "mod_date_to";
    //possible other paramethers
    /*
    public static final String PARAM_REGISTRAR_CODES = "registrar_codes";
    public static final String PARAM_IE_TYPES = "entity_types";
    public static final String PARAM_INDEX_ACTIVE_DOCS = "index_active_docs";
    public static final String PARAM_INDEX_DEACTIVED_DOCS = "index_deactivated_docs";
    */

    private final DateFormat dateFormat = new SimpleDateFormat("d. M. yyyy H:m.s");

    private SolrIndexer solrIndexer;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        super.interrupt();
        if (solrIndexer != null) {
            solrIndexer.stop();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.INDEXATION);
            logger.info("executing " + IndexationJob.class.getName());

            // czidlo api
            String czidloApiBaseUrl = (String) context.getMergedJobDataMap().get(PARAM_CZIDLO_API_BASE_URL);
            logger.info("Czidlo API base url: " + czidloApiBaseUrl);
            boolean czidloApiUseHttps = false;

            //solr
            String solrBaseUrl = (String) context.getMergedJobDataMap().get(PARAM_SOLR_BASE_URL);
            logger.info("Solr base url: " + solrBaseUrl);
            String solrCollection = (String) context.getMergedJobDataMap().get(PARAM_SOLR_COLLECTION);
            logger.info("Solr collection: " + solrCollection);
            String solrLogin = (String) context.getMergedJobDataMap().get(PARAM_SOLR_LOGIN);
            String solrPassword = (String) context.getMergedJobDataMap().get(PARAM_SOLR_PASSWORD);
            Boolean solrUseHttps = (Boolean) context.getMergedJobDataMap().get(PARAM_SOLR_USE_HTTPS);
            logger.info("Solr use https: " + solrUseHttps);

            //xslt
            File czidloToSolrXsltFile = new File((String) context.getMergedJobDataMap().get(PARAM_XSL_FILE));
            String czidloToSolrXslt = XmlTools.loadXmlFromFile(czidloToSolrXsltFile.getAbsolutePath());

            //data provider
            DataProvider dataProvider = new DataProvider() {
                @Override
                public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                    return Services.instanceOf().dataAccessService().digDocsByModificationDate(from, until);
                }

                @Override
                public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                    return Services.instanceOf().dataAccessService().urnByDigDocId(id, withPredecessorsAndSuccessors);
                }
            };

            // modification date from
            String modDateFromStr = (String) context.getMergedJobDataMap().getString(PARAM_MODIFICATION_DATE_FROM);
            logger.info("modification date from: " + modDateFromStr);
            DateTime modDateFrom = new DateTime(dateFormat.parse(modDateFromStr));

            // modification date to
            String modDateToStr = (String) context.getMergedJobDataMap().getString(PARAM_MODIFICATION_DATE_TO);
            logger.info("modification date to: " + modDateToStr);
            DateTime modDateTo = new DateTime(dateFormat.parse(modDateToStr));

            /*
            // registrars
            String registrarCodesStr = (String) context.getMergedJobDataMap().getString(PARAM_REGISTRAR_CODES);
            logger.info("registrars: " + registrarCodesStr);
            List<String> registrars = Arrays.asList(registrarCodesStr.split(","));
            // intelectual entity types
            String entityTypesStr = (String) context.getMergedJobDataMap().get(PARAM_IE_TYPES);
            List<String> entityTypes = Arrays.asList(entityTypesStr.split(","));
            logger.info("intelectual entity types: " + entityTypesStr);
            // urn:nbn states
            Boolean returnActive = context.getMergedJobDataMap().getBoolean(PARAM_INDEX_ACTIVE_DOCS);
            logger.info("return active records: " + returnActive);
            Boolean returnDeactivated = context.getMergedJobDataMap().getBoolean(PARAM_INDEX_DEACTIVED_DOCS);
            logger.info("return deactivated records: " + returnDeactivated);
            */

            //run
            logger.info("running Indexer process");
            solrIndexer = new SolrIndexer(czidloApiBaseUrl, czidloApiUseHttps,
                    solrBaseUrl, solrCollection, solrUseHttps, solrLogin, solrPassword,
                    dataProvider,
                    czidloToSolrXslt, czidloToSolrXsltFile,
                    buildReportLoggerOutputStream()
            );

            solrIndexer.setProgressListener(new ProgressListener() {
                @Override
                public void onProgress(int processed, int total) {
                    if (processed % 100 == 0) {
                        logger.info(String.format("Processed %d/%d", processed, total));
                    }
                }

                @Override
                public void onFinished(int processed, int total) {
                    logger.info(String.format("Processed %d/%d", processed, total));
                }
            });

            if (!interrupted) {
                solrIndexer.indexDocuments(modDateFrom, modDateTo);
            }
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
                logger.info("Indexer process killed");
            } else {
                context.setResult(ProcessState.FINISHED);
                logger.info("Indexer process finished, see report");
            }
        } catch (Throwable ex) {
            logger.error("Indexer process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    private OutputStream buildReportLoggerOutputStream() throws Exception {
        try {
            File reportFile = createWriteableProcessFile(PARAM_REPORT_FILE);
            return new FileOutputStream(reportFile);
        } catch (IOException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }

    @Override
    void close() {
        if (solrIndexer != null) {
            solrIndexer.close();
        }
    }

}
