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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;

/**
 * 
 * @author Martin Řehánek
 */
public class UrnNbnCsvExportJob extends AbstractJob {

    private static final String DATE_FORMAT = "d. M. yyyy H:m.s";
    private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public static final String CSV_EXPORT_FILE_NAME = "export.csv";

    public static final String PARAM_COUNTRY_CODE = "countryCode";
    public static final String PARAM_BEGIN = "begin";
    public static final String PARAM_END = "end";
    public static final String PARAM_REGISTRARS_CODES = "registrars";
    public static final String PARAM_ENT_TYPES = "entityTypes";
    public static final String PARAM_MISSING_CCNB = "missingCnb";
    public static final String PARAM_MISSING_ISSN = "missingIssn";
    public static final String PARAM_MISSING_ISBN = "missingIsbn";
    public static final String PARAM_RETURN_ACTIVE = "returnActive";
    public static final String PARAM_RETURN_DEACTIVED = "returnDeactivated";
    public static final String PARAM_EXPORT_NUM_OF_DIG_INSTANCES = "exportNumOfDigInstances";

    // TODO: i18n or keep here english only
    private static final String HEADER_TITLE = "Název titulu";
    private static final String HEADER_URN_NBN = "URN:NBN";
    private static final String HEADER_RESERVED = "Rezervováno";
    private static final String HEADER_REGISTERED = "Registrováno";
    private static final String HEADER_DEACTIVATED = "Deaktivováno";
    private static final String HEADER_ENT_TYPE = "Typ dokumentu";
    private static final String HEADER_HAS_CNB = "Má ČNB";
    private static final String HEADER_HAS_ISSN = "Má ISSN";
    private static final String HEADER_HAS_ISBN = "Má ISBN";
    private static final String HEADER_UNR_ACTIVE = "Aktivní";
    private static final String HEADER_NUM_OF_DIG_INSTANCES = "Počet instancí";

    private PrintWriter csvWriter;
    private Services services;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context.getMergedJobDataMap(), ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
            logger.info("executing " + UrnNbnCsvExportJob.class.getName());
            csvWriter = openCsvWriter(createWriteableProcessFile(CSV_EXPORT_FILE_NAME));
            this.services = initServices();
            logger.info("services initialized");
            String countryCode = context.getMergedJobDataMap().getString(PARAM_COUNTRY_CODE);
            logger.info("country code: " + countryCode);

            // datetime
            DateTime begin = parseDatetimeFromContext(PARAM_BEGIN, context);
            if (begin == null) {
                throw new NullPointerException("begin");
            }
            DateTime end = parseDatetimeFromContext(PARAM_END, context);
            if (end == null) {
                throw new NullPointerException("end");
            }
            logger.info("registered: " + begin + " - " + end);

            // registrars
            String registrarsStr = (String) context.getMergedJobDataMap().get(PARAM_REGISTRARS_CODES);
            if (registrarsStr == null) {
                throw new NullPointerException("registrars");
            }
            List<String> registrars = Arrays.asList((registrarsStr).split(","));
            logger.info("registrars: " + registrarsStr);

            // intelectual entity types
            String entityTypesStr = (String) context.getMergedJobDataMap().get(PARAM_ENT_TYPES);
            if (entityTypesStr == null) {
                throw new NullPointerException("intEntTypes");
            }
            List<String> entityTypes = Arrays.asList((entityTypesStr).split(","));
            logger.info("intelectual entity types: " + entityTypesStr);

            // limit to missing ccnb
            Boolean missingCcnb = context.getMergedJobDataMap().getBoolean(PARAM_MISSING_CCNB);
            if (missingCcnb == null) {
                throw new NullPointerException("missingCcnb");
            }
            logger.info("limit to records missing cCNB: " + missingCcnb);

            // limit to missing issn
            Boolean missingIssn = context.getMergedJobDataMap().getBoolean(PARAM_MISSING_ISSN);
            if (missingIssn == null) {
                throw new NullPointerException("missingIssn");
            }
            logger.info("limit to records missing ISSN: " + missingIssn);

            // limit to missing isbn
            Boolean missingIsbn = context.getMergedJobDataMap().getBoolean(PARAM_MISSING_ISBN);
            if (missingIsbn == null) {
                throw new NullPointerException("missingIsbn");
            }
            logger.info("limit to records missing ISBN: " + missingIsbn);

            // active/deactivated
            Boolean returnActive = context.getMergedJobDataMap().getBoolean(PARAM_RETURN_ACTIVE);
            if (returnActive == null) {
                throw new NullPointerException("returnActive");
            }
            logger.info("return active records: " + returnActive);
            Boolean returnDeactivated = context.getMergedJobDataMap().getBoolean(PARAM_RETURN_DEACTIVED);
            if (returnDeactivated == null) {
                throw new NullPointerException("returnDeactivated");
            }
            logger.info("return deactivated records: " + returnDeactivated);
            Boolean exportNumOfDigInstances = context.getMergedJobDataMap().getBoolean(PARAM_EXPORT_NUM_OF_DIG_INSTANCES);
            if (exportNumOfDigInstances == null) {
                throw new NullPointerException("includeNumOfDigInst");
            }

            // include number of digital instances
            logger.info("include number of digital instances: " + exportNumOfDigInstances);
            UrnNbnExportFilter filter = new UrnNbnExportFilter(begin, end, registrars, entityTypes, missingCcnb, missingIssn, missingIsbn,
                    returnActive, returnDeactivated);
            runProcess(countryCode, filter, exportNumOfDigInstances);
            logger.info("finished");
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
            } else {
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            // throw new JobExecutionException(ex);
            logger.error("urn:nbn export process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    private DateTime parseDatetimeFromContext(String key, JobExecutionContext context) throws ParseException {
        if (context.getMergedJobDataMap().containsKey(key)) {
            String val = context.getMergedJobDataMap().getString(key);
            if (val != null) {
                return new DateTime(dateFormat.parse(val));
            } else {
                throw new NullPointerException("data for key '" + key + "'");
            }
        } else {
            throw new IllegalStateException("no data for key '" + key + "' found");
        }
    }

    @Override
    void close() {
        if (csvWriter != null) {
            csvWriter.close();
        }
    }

    private PrintWriter openCsvWriter(File file) throws FileNotFoundException {
        return new PrintWriter(file);
    }

    private Services initServices() {
        Services.init(initDatabaseConnector());
        return Services.instanceOf();
    }

    private DatabaseConnector initDatabaseConnector() {
        return new PostgresPooledConnector();
    }

    private void runProcess(String countryCode, UrnNbnExportFilter filter, boolean exportNumOfDigInstances) {
        try {
            List<UrnNbnExport> urnNbnList = services.dataAccessService().selectByCriteria(countryCode, filter, exportNumOfDigInstances);
            csvWriter.println(buildHeaderLine(exportNumOfDigInstances));
            int counter = 0;
            int total = urnNbnList.size();
            logger.info("records to export: " + total);
            for (UrnNbnExport urnExport : urnNbnList) {
                if (interrupted) {
                    csvWriter.flush();
                    break;
                }
                String line = toCsvLine(urnExport, exportNumOfDigInstances);
                csvWriter.println(line);
                logger.info("exporting " + urnExport.getUrnNbn() + " (" + ++counter + "/" + total + ")");
            }
        } finally {
            csvWriter.close();
        }
    }

    private String buildHeaderLine(Boolean exportNumOfDigInstances) {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(HEADER_URN_NBN).append('\"').append(',');
        result.append('\"').append(HEADER_ENT_TYPE).append('\"').append(',');
        result.append('\"').append(HEADER_TITLE).append('\"').append(',');
        result.append('\"').append(HEADER_UNR_ACTIVE).append('\"').append(',');
        if (exportNumOfDigInstances) {
            result.append('\"').append(HEADER_NUM_OF_DIG_INSTANCES).append('\"').append(',');
        }
        result.append('\"').append(HEADER_RESERVED).append('\"').append(',');
        result.append('\"').append(HEADER_REGISTERED).append('\"').append(',');
        result.append('\"').append(HEADER_DEACTIVATED).append('\"').append(',');
        result.append('\"').append(HEADER_HAS_CNB).append('\"').append(',');
        result.append('\"').append(HEADER_HAS_ISSN).append('\"').append(',');
        result.append('\"').append(HEADER_HAS_ISBN).append('\"');
        return result.toString();
    }

    private String toCsvLine(UrnNbnExport item, Boolean exportNumOfDigInstances) {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(item.getUrnNbn()).append('\"').append(',');
        result.append('\"').append(item.getEntityType()).append('\"').append(',');
        result.append('\"').append(toAggregateTitle(item)).append('\"').append(',');

        result.append('\"').append(item.isActive()).append('\"').append(',');
        if (exportNumOfDigInstances) {
            result.append('\"').append(item.getNumberOfDigitalInstances()).append('\"').append(',');
        }
        result.append('\"').append(item.getReserved() == null ? "" : item.getReserved()).append('\"').append(',');
        result.append('\"').append(item.getRegistered()).append('\"').append(',');
        result.append('\"').append(item.getDeactivated() == null ? "" : item.getDeactivated()).append('\"').append(',');
        result.append('\"').append(item.isCnbAssigned()).append('\"').append(',');
        result.append('\"').append(item.isIssnAssigned()).append('\"').append(',');
        result.append('\"').append(item.isIsbnAssigned()).append('\"');
        return result.toString();
    }

    private String toAggregateTitle(UrnNbnExport export) {
        StringBuilder builder = new StringBuilder();
        switch (EntityType.valueOf(export.getEntityType())) {
        case MONOGRAPH:
        case PERIODICAL:
        case ANALYTICAL:
        case THESIS:
        case OTHER:
            builder.append(export.getTitle());
            if (export.getSubtitle() != null) {
                builder.append(" (").append(export.getSubtitle()).append(')');
            }
            return builder.toString();
        case MONOGRAPH_VOLUME:
        case PERIODICAL_VOLUME:
            builder.append(export.getTitle());
            builder.append(", ").append(export.getVolumeTitle());
            return builder.toString();
        case PERIODICAL_ISSUE:
            builder.append(export.getTitle());
            builder.append(", ").append(export.getVolumeTitle());
            builder.append(", ").append(export.getIssueTitle());
            return builder.toString();
        default:
            return "";
        }
    }

}
