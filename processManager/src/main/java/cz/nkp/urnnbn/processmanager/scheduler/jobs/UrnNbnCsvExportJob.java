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

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Martin Řehánek
 */
public class UrnNbnCsvExportJob extends AbstractJob {

    public static final String CSV_EXPORT_FILE_NAME = "export.csv";

    // TODO: really needed?
    public static final String PARAM_COUNTRY_CODE = "countryCode";
    public static final String PARAM_BEGIN = "begin";
    public static final String PARAM_END = "end";
    public static final String PARAM_REGISTRARS_CODES = "registrars";
    public static final String PARAM_ENT_TYPES = "entityTypes";
    public static final String PARAM_WITH_MISSING_CCNB_ONLY = "withMissingCnbOnly";
    public static final String PARAM_WITH_MISSING_ISSN_ONLY = "withMissingIssnOnly";
    public static final String PARAM_WITH_MISSING_ISBN_ONLY = "withMissingIsbnOnly";
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

    private final DateFormat dateFormat = new SimpleDateFormat("d. M. yyyy H:m.s");
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
            // country code
            String countryCode = context.getMergedJobDataMap().getString(PARAM_COUNTRY_CODE);
            logger.info("country code: " + countryCode);
            //filter
            UrnNbnExportFilter filter = extractFilter(context);
            // include number of digital instances
            Boolean exportNumOfDigInstances = context.getMergedJobDataMap().getBoolean(PARAM_EXPORT_NUM_OF_DIG_INSTANCES);
            if (exportNumOfDigInstances == null) {
                throw new NullPointerException("includeNumOfDigInst");
            }
            logger.info("export number of digital instances: " + exportNumOfDigInstances);
            //run
            runProcess(countryCode, filter, exportNumOfDigInstances);
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
                logger.info("urn:nbn export process killed");
            } else {
                context.setResult(ProcessState.FINISHED);
                logger.info("urn:nbn export process finished");
            }
        } catch (Throwable ex) {
            // throw new JobExecutionException(ex);
            logger.error("urn:nbn export process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    private UrnNbnExportFilter extractFilter(JobExecutionContext context) throws ParseException {
        UrnNbnExportFilter result = new UrnNbnExportFilter();
        // registration datestamps
        result.setBegin(parseDatetimeOrNullFromContext(PARAM_BEGIN, context, dateFormat));
        result.setEnd(parseDatetimeOrNullFromContext(PARAM_END, context, dateFormat));
        logger.info("registered: " + ((result.getBegin() == null && result.getEnd() == null) ? "ALL" : result.getBegin() + " - " + result.getEnd()));
        // registrars
        result.setRegistrars(parseStringListOrNullFromContext(PARAM_REGISTRARS_CODES, context));
        logger.info("registrars: " + (result.getRegistrars() == null ? "ALL" : listOfStringsToString(result.getRegistrars())));
        // intelectual entity types
        result.setEntityTypes(parseStringListOrNullFromContext(PARAM_ENT_TYPES, context));
        logger.info("intelectual entity types: " + (result.getEntityTypes() == null ? "ALL" : listOfStringsToString(result.getEntityTypes())));
        // limit to only those with missing ccnb
        result.setWithMissingCcnbOnly(context.getMergedJobDataMap().getBoolean(PARAM_WITH_MISSING_CCNB_ONLY));
        logger.info("limit to records missing cCNB: " + result.getWithMissingCcnbOnly());
        // limit to only those with missing issn
        result.setWithMissingIssnOnly(context.getMergedJobDataMap().getBoolean(PARAM_WITH_MISSING_ISSN_ONLY));
        logger.info("limit to records missing ISSN: " + result.getWithMissingIssnOnly());
        // limit to only those with missing isbn
        result.setWithMissingIsbnOnly(context.getMergedJobDataMap().getBoolean(PARAM_WITH_MISSING_ISBN_ONLY));
        logger.info("limit to records missing ISBN: " + result.getWithMMissingIsbnOnly());
        // urn:nbn states
        result.setReturnActive(context.getMergedJobDataMap().getBoolean(PARAM_RETURN_ACTIVE));
        logger.info("return active records: " + result.getReturnActive());
        result.setReturnDeactivated(context.getMergedJobDataMap().getBoolean(PARAM_RETURN_DEACTIVED));
        logger.info("return deactivated records: " + result.getReturnDeactivated());
        return result;
    }


    @Override
    void close() {
        if (csvWriter != null) {
            csvWriter.close();
        }
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
