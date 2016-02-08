package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;

public class DiUrlAvailabilityCheckJob extends AbstractJob {

    public static final String CSV_EXPORT_FILE_NAME = "di_check.csv";

    public static final String PARAM_COUNTRY_CODE = "countryCode";
    public static final String PARAM_REGISTRAR_CODES = "registrarCodes";
    public static final String PARAM_INT_ENT_TYPES = "intEntTypes";
    public static final String PARAM_URNNBN_STATES_INCLUDE_ACTIVE = "urnNbnStatesIncludeActive";
    public static final String PARAM_URNNBN_STATES_INCLUDE_DEACTIVATED = "urnNbnStatesIncludeDeactivated";
    public static final String PARAM_DI_STATES_INCLUDE_ACTIVE = "diStatesIncludeActive";
    public static final String PARAM_DI_STATES_INCLUDE_DEACTIVATED = "diStatesIncludeDeactivated";
    public static final String PARAM_DI_DATESTAMP_FROM = "diDsFrom";
    public static final String PARAM_DI_DATESTAMP_TO = "diDsTo";

    private static final String HEADER_URN_NBN = "URN:NBN";
    private static final String HEADER_URN_NBN_STATE = "Stav URN:NBN";
    private static final String HEADER_INT_ENT_TYPE = "Typ dokumentu";
    private static final String HEADER_DI_URL = "URL";
    private static final String HEADER_DI_FORMAT = "Formát";
    private static final String HEADER_DI_ACCESSIBLITY = "Dostupnost";
    private static final String HEADER_DI_CREATED = "Vytvořeno";
    private static final String HEADER_DI_DEACTIVATED = "Deaktivováno";
    private static final String HEADER_AVAILABILITY_RESULT = "Výsledek testu dostupnosti URL";

    private final DateFormat dateFormat = new SimpleDateFormat("d. M. yyyy H:m.s");
    private PrintWriter csvWriter;
    private Services services;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context.getMergedJobDataMap(), ProcessType.DI_URL_AVAILABILITY_CHECK);
            logger.info("executing " + DiUrlAvailabilityCheckJob.class.getName());
            csvWriter = openCsvWriter(createWriteableProcessFile(CSV_EXPORT_FILE_NAME));
            this.services = initServices();
            logger.info("services initialized");
            String countryCode = context.getMergedJobDataMap().getString(PARAM_COUNTRY_CODE);
            logger.info("country code: " + countryCode);
            Filter filter = extractFilter(context);
            runProcess(countryCode, filter);
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

    private Filter extractFilter(JobExecutionContext context) throws ParseException {
        Filter result = new Filter();
        // registrars
        String registrarCodesStr = context.getMergedJobDataMap().getString(PARAM_REGISTRAR_CODES);
        logger.info("registrars: " + registrarCodesStr);
        result.setRegistrarCodes(Arrays.asList(registrarCodesStr.split(",")));
        // intelectual entity types
        String entityTypesStr = context.getMergedJobDataMap().getString(PARAM_INT_ENT_TYPES);
        logger.info("intelectual entity types: " + entityTypesStr);
        result.setEntityTypes(Arrays.asList(entityTypesStr.split(",")));
        // states
        result.setUrnStateIncludeActive(context.getMergedJobDataMap().getBoolean(PARAM_URNNBN_STATES_INCLUDE_ACTIVE));
        logger.info("URN:NBN - include active: " + result.getUrnStateIncludeActive());
        result.setUrnStateIncludeDeactivated(context.getMergedJobDataMap().getBoolean(PARAM_URNNBN_STATES_INCLUDE_DEACTIVATED));
        logger.info("URN:NBN - include deactivated: " + result.getUrnStateIncludeDeactivated());
        result.setDiStateIncludeActive(context.getMergedJobDataMap().getBoolean(PARAM_DI_STATES_INCLUDE_ACTIVE));
        logger.info("DI - include active: " + result.getDiStateIncludeActive());
        result.setDiStateIncludeDeactivated(context.getMergedJobDataMap().getBoolean(PARAM_DI_STATES_INCLUDE_ACTIVE));
        logger.info("DI - include deactivated: " + result.getDiStateIncludeDeactivated());
        // datestamps
        result.setDiDsFrom(parseDatetimeFromContext(PARAM_DI_DATESTAMP_FROM, context, dateFormat));
        result.setDiDsTo(parseDatetimeFromContext(PARAM_DI_DATESTAMP_TO, context, dateFormat));
        logger.info("date range: " + result.getDiDsFrom() + " - " + result.getDiDsTo());
        return result;
    }

    private void runProcess(String countryCode, Filter filter/* String countryCode, UrnNbnExportFilter filter, boolean exportNumOfDigInstances */) {
        try {
            // List<UrnNbnExport> urnNbnList = services.dataAccessService().selectByCriteria(countryCode, filter, exportNumOfDigInstances);
            // csvWriter.println(buildHeaderLine(exportNumOfDigInstances));
            csvWriter.println(buildHeaderLine());
            // int counter = 0;
            // int total = urnNbnList.size();
            // logger.info("records to export: " + total);
            // for (UrnNbnExport urnExport : urnNbnList) {
            // if (interrupted) {
            // csvWriter.flush();
            // break;
            // }
            // String line = toCsvLine(urnExport, exportNumOfDigInstances);
            // csvWriter.println(line);
            // logger.info("exporting " + urnExport.getUrnNbn() + " (" + ++counter + "/" + total + ")");
            // }
        } finally {
            csvWriter.close();
        }
    }

    private String buildHeaderLine(/* Boolean exportNumOfDigInstances */) {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(HEADER_URN_NBN).append('\"').append(',');
        // TODO: only if not filtered
        result.append('\"').append(HEADER_URN_NBN_STATE).append('\"').append(',');
        result.append('\"').append(HEADER_INT_ENT_TYPE).append('\"').append(',');
        result.append('\"').append(HEADER_DI_URL).append('\"').append(',');
        // TODO: state
        result.append('\"').append(HEADER_DI_FORMAT).append('\"').append(',');
        result.append('\"').append(HEADER_DI_ACCESSIBLITY).append('\"').append(',');
        result.append('\"').append(HEADER_DI_CREATED).append('\"').append(',');
        result.append('\"').append(HEADER_DI_DEACTIVATED).append('\"').append(',');
        result.append('\"').append(HEADER_AVAILABILITY_RESULT).append('\"');
        return result.toString();
    }

    @Override
    void close() {
        if (csvWriter != null) {
            csvWriter.close();
        }
    }

    class Filter {

        private List<String> registrarCodes;
        private List<String> entityTypes;
        private Boolean urnStateIncludeActive;
        private Boolean urnStateIncludeDeactivated;
        private Boolean diStateIncludeActive;
        private Boolean diStateIncludeDeactivated;
        private DateTime diDsFrom;
        private DateTime diDsTo;

        public List<String> getRegistrarCodes() {
            return registrarCodes;
        }

        public void setRegistrarCodes(List<String> registrarCodes) {
            if (registrarCodes == null) {
                throw new NullPointerException("registrarCodes");
            }
            this.registrarCodes = registrarCodes;
        }

        public List<String> getEntityTypes() {
            return entityTypes;
        }

        public void setEntityTypes(List<String> entityTypes) {
            if (entityTypes == null) {
                throw new NullPointerException("entityTypes");
            }
            this.entityTypes = entityTypes;
        }

        public Boolean getUrnStateIncludeActive() {
            return urnStateIncludeActive;
        }

        public void setUrnStateIncludeActive(Boolean urnStateIncludeActive) {
            if (urnStateIncludeActive == null) {
                throw new NullPointerException("urnStateIncludeActive");
            }
            this.urnStateIncludeActive = urnStateIncludeActive;
        }

        public Boolean getUrnStateIncludeDeactivated() {
            return urnStateIncludeDeactivated;
        }

        public void setUrnStateIncludeDeactivated(Boolean urnStateIncludeDeactivated) {
            if (urnStateIncludeDeactivated == null) {
                throw new NullPointerException("urnStateIncludeDeactivated");
            }
            this.urnStateIncludeDeactivated = urnStateIncludeDeactivated;
        }

        public Boolean getDiStateIncludeActive() {
            return diStateIncludeActive;
        }

        public void setDiStateIncludeActive(Boolean diStateIncludeActive) {
            if (diStateIncludeActive == null) {
                throw new NullPointerException("diStateIncludeActive");
            }
            this.diStateIncludeActive = diStateIncludeActive;
        }

        public Boolean getDiStateIncludeDeactivated() {
            return diStateIncludeDeactivated;
        }

        public void setDiStateIncludeDeactivated(Boolean diStateIncludeDeactivated) {
            if (diStateIncludeDeactivated == null) {
                throw new NullPointerException("diStateIncludeDeactivated");
            }
            this.diStateIncludeDeactivated = diStateIncludeDeactivated;
        }

        public DateTime getDiDsFrom() {
            return diDsFrom;
        }

        public void setDiDsFrom(DateTime diDsFrom) {
            if (diDsFrom == null) {
                throw new NullPointerException("diDsFrom");
            }
            this.diDsFrom = diDsFrom;
        }

        public DateTime getDiDsTo() {
            return diDsTo;
        }

        public void setDiDsTo(DateTime diDsTo) {
            if (diDsTo == null) {
                throw new NullPointerException("diDsTo");
            }
            this.diDsTo = diDsTo;
        }
    }

}
