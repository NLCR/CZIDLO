package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cz.nkp.urnnbn.core.DiExport;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.oaiadapter.czidlo.UrnnbnStatus;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.DiUrlAvailabilityCheckJob.UrlChecker.Result;
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

    // TODO: i18n or keep here english only
    private static final String HEADER_URN_NBN = "URN:NBN";
    private static final String HEADER_URN_NBN_STATE = "Stav URN:NBN";
    private static final String HEADER_INT_ENT_TYPE = "Typ dokumentu";
    private static final Object HEADER_DI_ACTIVE = "DI aktivní";
    private static final String HEADER_DI_FORMAT = "Formát";
    private static final String HEADER_DI_ACCESSIBLITY = "Přístupnost";
    private static final String HEADER_DI_CREATED = "Vytvořeno";
    private static final String HEADER_DI_DEACTIVATED = "Deaktivováno";
    private static final String HEADER_DI_URL = "URL";
    private static final Object HEADER_AVAILABILITY_RESULT_CODE = "Dostupnost URL";
    private static final Object HEADER_AVAILABILITY_RESULT_MESSAGE = "Popis chyby";

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
        result.setDiStateIncludeDeactivated(context.getMergedJobDataMap().getBoolean(PARAM_DI_STATES_INCLUDE_DEACTIVATED));
        logger.info("DI - include deactivated: " + result.getDiStateIncludeDeactivated());
        // datestamps
        result.setDiDsFrom(parseDatetimeFromContext(PARAM_DI_DATESTAMP_FROM, context, dateFormat));
        result.setDiDsTo(parseDatetimeFromContext(PARAM_DI_DATESTAMP_TO, context, dateFormat));
        logger.info("date range: " + result.getDiDsFrom() + " - " + result.getDiDsTo());
        return result;
    }

    private void runProcess(String countryCode, Filter filter) {
        try {
            List<DiExport> exports = services.dataAccessService().listDiExport(filter.getRegistrarCodes(), filter.getEntityTypes(),
                    filter.urnStateIncludeActive, filter.urnStateIncludeDeactivated, filter.diStateIncludeActive, filter.diStateIncludeDeactivated);
            // header
            csvWriter.println(buildHeaderLine());
            // records
            logger.info("records to export: " + exports.size());
            int counter = 0;
            for (DiExport export : exports) {
                counter++;
                if (interrupted) {
                    csvWriter.flush();
                    break;
                }
                Result checkResult = new UrlChecker(export.getDiUrl()).check();
                String line = toCsvLine(export, checkResult);
                csvWriter.println(line);
                if (counter % 10 == 0) {
                    int percentage = (int) ((((float) counter) / exports.size()) * 100);
                    logger.info(String.format("processed %d/%d (%d %%)", counter, exports.size(), percentage));
                }
            }
            if (counter % 10 != 0) {
                int percentage = (int) ((((float) counter) / exports.size()) * 100);
                logger.info(String.format("processed %d/%d (%d %%)", counter, exports.size(), percentage));
            }

        } finally {
            csvWriter.close();
        }
    }

    static class UrlChecker {
        private static final int MAX_REDIRECTIONS = 5;// TODO: presmerovani
        private static final int TIMEOUT_CONNECTION = 1000;
        private static final int TIMEOUT_READ = 3000;

        private final String urlString;

        public UrlChecker(String urlString) {
            this.urlString = urlString;
        }

        public Result check() {
            return check(urlString, MAX_REDIRECTIONS);
        }

        private Result check(String urlString, int remainingRedirections) {
            HttpURLConnection urlConnection = null;
            if (remainingRedirections == 0) {
                return new Result("TO_MANY_REDIRECTIONS", "Reached " + MAX_REDIRECTIONS + " redirections, probably redirection loop.");
            } else {
                try {
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(TIMEOUT_CONNECTION);
                    urlConnection.setReadTimeout(TIMEOUT_READ);
                    // urlConnection.setr
                    int responseCode = urlConnection.getResponseCode();
                    switch (responseCode) {
                    case 200:
                        return new Result("OK", "");
                    case 300:
                    case 301:
                    case 302:
                    case 303:
                    case 305:
                    case 307:
                        String location = urlConnection.getHeaderField("Location");
                        return check(location, remainingRedirections - 1);

                    default:
                        return new Result("UNEXPECTED_HTTP_CODE", "" + responseCode + " " + urlConnection.getResponseMessage());
                    }
                } catch (MalformedURLException e) {
                    return new Result("INVALID_URL", e.getMessage());
                } catch (UnknownHostException e) {
                    return new Result("UNKNOWN_HOST", "Domain " + e.getMessage() + " not available.");
                } catch (SocketTimeoutException e) {
                    return new Result("TIMEOUT", String.format("Reached either connection timeout (%d ms) or data transfer timeout (%d ms).",
                            TIMEOUT_CONNECTION, TIMEOUT_READ));
                } catch (IOException e) {
                    return new Result("OTHER_ERROR", e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }

        static class Result {
            private String state;
            private String message;

            public Result(String state, String message) {
                this.state = state;
                this.message = message;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

        }

    }

    private String buildHeaderLine() {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(HEADER_URN_NBN).append('\"').append(',');
        result.append('\"').append(HEADER_URN_NBN_STATE).append('\"').append(',');
        result.append('\"').append(HEADER_INT_ENT_TYPE).append('\"').append(',');
        result.append('\"').append(HEADER_DI_ACTIVE).append('\"').append(',');
        result.append('\"').append(HEADER_DI_FORMAT).append('\"').append(',');
        result.append('\"').append(HEADER_DI_ACCESSIBLITY).append('\"').append(',');
        result.append('\"').append(HEADER_DI_CREATED).append('\"').append(',');
        result.append('\"').append(HEADER_DI_DEACTIVATED).append('\"').append(',');
        result.append('\"').append(HEADER_DI_URL).append('\"').append(',');
        result.append('\"').append(HEADER_AVAILABILITY_RESULT_CODE).append('\"').append(',');
        result.append('\"').append(HEADER_AVAILABILITY_RESULT_MESSAGE).append('\"');
        return result.toString();
    }

    private String toCsvLine(DiExport export, Result checkResult) {
        StringBuilder result = new StringBuilder();
        UrnNbn urnNbn = new UrnNbn(RegistrarCode.valueOf(export.getRegistrarCode()), export.getDocumentCode(), -1L, null);
        result.append('\"').append(urnNbn.toString()).append('\"').append(',');
        UrnnbnStatus status = export.isUrnActive() ? UrnnbnStatus.ACTIVE : UrnnbnStatus.DEACTIVATED;
        result.append('\"').append(status.toString()).append('\"').append(',');
        result.append('\"').append(export.getIeType()).append('\"').append(',');
        result.append('\"').append(export.isDiActive()).append('\"').append(',');
        result.append('\"').append(stringValueOrEmpty(export.getDiFormat())).append('\"').append(',');
        result.append('\"').append(stringValueOrEmpty(export.getDiAccessiblility())).append('\"').append(',');
        result.append('\"').append(export.getDiCreated().toString()).append('\"').append(',');
        result.append('\"');
        if (export.getDiDeactivated() != null) {
            result.append(export.getDiDeactivated().toString());
        }
        result.append('\"').append(',');
        result.append('\"').append(export.getDiUrl()).append('\"').append(',');
        result.append('\"').append(checkResult.getState()).append('\"').append(',');
        result.append('\"').append(checkResult.getMessage()).append('\"');
        return result.toString();
    }

    private String stringValueOrEmpty(String value) {
        return value == null ? "" : value;
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
