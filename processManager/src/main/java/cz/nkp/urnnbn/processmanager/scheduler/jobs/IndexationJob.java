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

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import cz.nkp.urnnbn.indexer.es.EsIndexerBatching;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.indexer.DataProvider;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import cz.nkp.urnnbn.indexer.ProgressListener;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    //elasticsearch
    public static final String PARAM_ES_BASE_URL = "esBaseUrl";
    public static final String PARAM_ES_LOGIN = "esLogin";
    public static final String PARAM_ES_PASSWORD = "esPassword";
    public static final String PARAM_ES_INDEX_SEARCH_NAME = "esIndexSearchName";
    public static final String PARAM_ES_INDEX_ASSIGN_NAME = "esIndexAssignName";
    public static final String PARAM_ES_INDEX_RESOLVE_NAME = "esIndexResolveName";

    //database
    public static final String PARAM_CZIDLO_DB_URL = "dbUrl";
    public static final String PARAM_CZIDLO_DB_LOGIN = "dbLogin";
    public static final String PARAM_CZIDLO_DB_PASSWORD = "dbPassword";

    //date range
    public static final String PARAM_MODIFICATION_DATE_FROM = "mod_date_from";
    public static final String PARAM_MODIFICATION_DATE_TO = "mod_date_to";
    //possiblly other paramethers

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private EsIndexer esIndexer;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        super.interrupt();
        if (esIndexer != null) {
            esIndexer.stop();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.INDEXATION);
            logger.info("Executing " + IndexationJob.class.getSimpleName());

            IndexerConfig config = new IndexerConfig();

            //elasticsearch
            config.setEsApiBaseUrl((String) context.getMergedJobDataMap().get(PARAM_ES_BASE_URL));
            logger.info("Elasticsearch base url: " + config.getEsApiBaseUrl());
            config.setEsApiIndexSearchName((String) context.getMergedJobDataMap().get(PARAM_ES_INDEX_SEARCH_NAME));
            logger.info("ES index (search) name: " + config.getEsApiIndexSearchName());
            config.setEsApiIndexAssignName((String) context.getMergedJobDataMap().get(PARAM_ES_INDEX_ASSIGN_NAME));
            logger.info("ES index (assign) name: " + config.getEsApiIndexAssignName());
            config.setEsApiIndexResolveName((String) context.getMergedJobDataMap().get(PARAM_ES_INDEX_RESOLVE_NAME));
            logger.info("ES index (resolve) name: " + config.getEsApiIndexResolveName());
            config.setEsApiLogin((String) context.getMergedJobDataMap().get(PARAM_ES_LOGIN));
            config.setEsApiPassword((String) context.getMergedJobDataMap().get(PARAM_ES_PASSWORD));
            //logger.info("Elasticsearch login: " + config.getEsApiLogin() + ", password:" +  config.getEsApiPassword());

            //czidlo database
            config.setDbUrl((String) context.getMergedJobDataMap().get(PARAM_CZIDLO_DB_URL));
            logger.info("Database url: " + config.getDbUrl());
            config.setDbLogin((String) context.getMergedJobDataMap().get(PARAM_CZIDLO_DB_LOGIN));
            config.setDbPassword((String) context.getMergedJobDataMap().get(PARAM_CZIDLO_DB_PASSWORD));
            //logger.info("Database login: " + config.getDbLogin() + ", password:" +  config.getDbPassword());

            //registration date range
            DateTime registrationStartRaw = parseDatetimeOrNullFromContext(PARAM_MODIFICATION_DATE_FROM, context, dateFormat);
            DateTime registrationEndRaw = parseDatetimeOrNullFromContext(PARAM_MODIFICATION_DATE_TO, context, dateFormat);

            DateTime registrationStart = registrationStartRaw == null
                    ? null
                    : registrationStartRaw.toLocalDate()
                    .toDateTimeAtStartOfDay(registrationStartRaw.getZone());
            DateTime registrationEnd = registrationEndRaw == null
                    ? null
                    : registrationEndRaw.toLocalDate()
                    .plusDays(1)
                    .toDateTimeAtStartOfDay(registrationEndRaw.getZone())
                    .minusMillis(1);

            logger.info("Registration from: " + (registrationStart == null ? null : registrationStart.toString(dateTimeFormatter)));
            logger.info("Registration until: " + (registrationEnd == null ? null : registrationEnd.toString(dateTimeFormatter)));

            DateTime registrationAfterEnd = registrationEnd == null ? null : registrationEnd.plusMillis(1);
            //System.out.println("until: " + registrationEnd + ", after until: " + registrationAfterEnd);

            esIndexer = new EsIndexerBatching(config, buildReportLoggerOutputStream(),
                    new DataProvider() {
                        @Override
                        public List<DigitalDocument> digDocsByModificationDate(DateTime fromInclusive, DateTime untilExclusive) {
                            return Services.instanceOf().dataAccessService().digDocsByModificationDate(fromInclusive, untilExclusive);
                        }

                        @Override
                        public List<ResolvationLog> resolvationLogsByDate(DateTime fromInclusive, DateTime untilExclusive) {
                            return Services.instanceOf().dataAccessService().resolvationLogsByDate(fromInclusive, untilExclusive);
                        }

                        @Override
                        public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                            return Services.instanceOf().dataAccessService().urnByDigDocId(id, withPredecessorsAndSuccessors);
                        }
                    }
            );

            logger.info("\nIndexing digital documents");
            esIndexer.setProgressListener(new ProgressListener() {
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
                esIndexer.indexDigitalDocuments(registrationStart, registrationEnd);
            }

            logger.info("\nIndexing resolvation logs");
            esIndexer.setProgressListener(new ProgressListener() {
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
                esIndexer.indexResolvationLogs(registrationStart, registrationAfterEnd);
            }

            if (interrupted) {
                logger.info("Process killed");
                context.setResult(ProcessState.KILLED);
            } else {
                logger.info("Process finished, see report file");
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            logger.error("Process failed", ex);
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
        if (esIndexer != null) {
            esIndexer.close();
        }
        closeLogger();
    }

}
