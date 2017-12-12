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

import cz.nkp.urnnbn.oaiadapter.ReportLogger;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Martin Řehánek
 */
public class IndexationJob extends AbstractJob {

    public static final String PARAM_REPORT_FILE = "report.txt";
    public static final String PARAM_MODIFICATION_DATE_FROM = "mod_date_from";
    public static final String PARAM_MODIFICATION_DATE_TO = "mod_date_to";

    //possible future params
    /*
    public static final String PARAM_REGISTRAR_CODES = "registrar_codes";
    public static final String PARAM_IE_TYPES = "entity_types";
    public static final String PARAM_INDEX_ACTIVE_DOCS = "index_active_docs";
    public static final String PARAM_INDEX_DEACTIVED_DOCS = "index_deactivated_docs";
    */

    private final DateFormat dateFormat = new SimpleDateFormat("d. M. yyyy H:m.s");

    private ReportLogger reportLogger;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
            init(context.getMergedJobDataMap(), ProcessType.INDEXATION);
            logger.info("executing " + IndexationJob.class.getName());
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

            //prepare report logger
            File reportFile = createWriteableProcessFile(PARAM_REPORT_FILE);
            reportLogger = buildReportLogger(reportFile);
            //run
            logger.info("running Indexer process");
            run(modDateFrom, modDateTo);
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

    private void run(DateTime modDateFrom, DateTime modDateTo) {
        // TODO: 12.12.17 actual process implementation here
        logger.info("sorry, actual process not implemented yet");
        reportLogger.report("todo: actual report here");
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
