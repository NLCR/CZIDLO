package cz.nkp.urnnbn.indexer.es;

import com.zaxxer.hikari.HikariDataSource;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EsIndexer implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(EsIndexer.class.getName());

    private final DataProvider dataProvider;
    private final ReportLogger reportLogger;

    //status info
    private boolean stopped = false;
    private ProgressListener progressListener;
    private long initTime;

    //helpers
    private EsConnector esConnector = null;
    private HikariDataSource dataSource;

    private final IndexerConfig config;

    public EsIndexer(IndexerConfig config, OutputStream reportLoggerStream, DataProvider dataProvider) {
        long start = System.currentTimeMillis();
        this.reportLogger = new ReportLogger(reportLoggerStream);
        this.dataProvider = dataProvider;
        this.config = config;
        String baseUrl = config.getEsApiBaseUrl();
        String login = config.getEsApiLogin();
        String password = config.getEsApiPassword();
        String indexSearch = config.getEsApiIndexSearchName();
        String indexAssign = config.getEsApiIndexAssignName();
        String indexResolve = config.getEsApiIndexResolveName();

        logger.info("Initializing Elasticsearch client for URL: " + baseUrl);
        this.dataSource = Utils.createPooledDataSource(
                config.getDbUrl(),
                config.getDbLogin(),
                config.getDbPassword()
        );
        this.esConnector = new EsConnector(baseUrl, login, password,
                indexSearch, indexAssign, indexResolve,
                this.dataSource
        );
        this.initTime = System.currentTimeMillis() - start;
    }

    public void close() {
        if (reportLogger != null) {
            reportLogger.close();
        }
        if (esConnector != null) {
            esConnector.close();
            esConnector = null;
        }
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void indexResolvation(ResolvationLog resolvationLog) {
        try {
            esConnector.indexResolvation(resolvationLog.getId(), reportLogger);
        } catch (SQLException e) {
            report(" SQL error", e);
        } catch (IOException e) {
            report(" I/O error", e);
        }
    }

    public void indexDigitalDocument(long ddInternalId) {
        //System.out.println("Indexing digital document with internal id " + ddInternalId);
        //System.out.println("indexer base url: " + config.getEsApiBaseUrl());
        //System.out.println("indexer search index:  " + config.getEsApiIndexSearchName());
        indexDigitalDocument(ddInternalId, new Counters(1), true);
    }

    private void indexDigitalDocument(long ddInternalId, Counters counters, boolean explicitCommit) {
        UrnNbn urnNbn = dataProvider.urnByDigDocId(ddInternalId, false);
        if (urnNbn == null) {
            report(" digital document with id " + ddInternalId + " is missing URN:NBN");
        } else {
            //report(" processing " + urnNbn);
            try {
                esConnector.indexDigitalDocument(ddInternalId, reportLogger);
                counters.incrementIndexed();
            } catch (IOException e) {
                counters.incrementErrors();
                report(" I/O error", e);
            } catch (SQLException e) {
                counters.incrementErrors();
                report(" SQL error", e);
            } catch (Throwable e) {
                counters.incrementErrors();
                report(" Unexpected error", e);
            }
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    public void indexDigitalDocuments(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        report("Indexing digital documents");
        report("==============================");

        //range: dd.mm.yyyy - dd.mm.yyyy
        String fromStr = from == null ? null : from.toString("dd.MM.yyyy");
        String toStr = to == null ? null : to.toString("dd.MM.yyyy");
        report(String.format("Range: %s - %s", fromStr, toStr));
        List<DigitalDocument> digitalDocuments = dataProvider.digDocsByModificationDate(from, to);
        report("Matching records: " + digitalDocuments.size());

        Counters counters = new Counters(digitalDocuments.size());
        Integer limit = null; // for testing, set to null for production
        int iterationCount = 0;

        for (DigitalDocument doc : digitalDocuments) {
            if (stopped) {
                report(" stopped ");
                break;
            }
            if (limit != null && iterationCount++ >= limit) {
                report(" limit of " + limit + " reached, stopping (for testing purposes) ");
                break;
            }
            indexDigitalDocument(doc.getId(), counters, false);
        }
        commit(); //one explicit commit at the very end

        long now = System.currentTimeMillis();
        long totalDuration = now - start;
        float durationPerRecord = counters.getProcessed() == 0 ? 0 : (float) totalDuration / counters.getProcessed();

        report("Results");
        report("------------------------------");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization time: " + formatTime(initTime));
        report(" records processing time: " + formatTime(totalDuration));
        report(" avg. record processing time: " + String.format("%.2f ms", durationPerRecord));
        report("");
        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
        }
    }

    public void indexResolvationLogs(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        report("Indexing resolvation logs");
        report("==============================");

        //range: dd.mm.yyyy - dd.mm.yyyy
        String fromStr = from == null ? null : from.toString("dd.MM.yyyy");
        String toStr = to == null ? null : to.toString("dd.MM.yyyy");
        report(String.format("Range: %s - %s", fromStr, toStr));
        List<ResolvationLog> resolvationLogs = dataProvider.resolvationLogsByDate(from, to);
        report("Matching records: " + resolvationLogs.size());

        Counters counters = new Counters(resolvationLogs.size());
        Integer limit = null; // for testing, set to null for production
        int iterationCount = 0;
        for (ResolvationLog log : resolvationLogs) {
            if (stopped) {
                report(" stopped ");
                break;
            }
            if (limit != null && iterationCount++ >= limit) {
                report(" limit of " + limit + " reached, stopping (for testing purposes) ");
                break;
            }
            indexResolvationLog(log, counters, false);
        }
        commit(); //one explicit commit at the very end

        long now = System.currentTimeMillis();
        long totalDuration = now - start;
        float durationPerRecord = counters.getProcessed() == 0 ? 0 : (float) totalDuration / counters.getProcessed();

        report("Results");
        report("------------------------------");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization time: " + formatTime(initTime));
        report(" records processing time: " + formatTime(totalDuration));
        report(" avg. record processing time: " + String.format("%.2f ms", durationPerRecord));
        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
        }
        report("");
    }

    private void indexResolvationLog(ResolvationLog resolvationLog, Counters counters, boolean explicitCommit) {
        //report(" processing " + resolvationLog);
        try {
            esConnector.indexResolvation(resolvationLog.getId(), reportLogger);
            counters.incrementIndexed();
        } catch (IOException e) {
            counters.incrementErrors();
            report(" I/O error", e);
        } catch (SQLException e) {
            counters.incrementErrors();
            report(" SQL error", e);
        } catch (Throwable e) {
            counters.incrementErrors();
            report(" Unexpected error", e);
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    private String formatTime(long millis) {
        long hours = millis / (60 * 60 * 1000);
        long minutes = millis / (60 * 1000) - hours * 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    private void report(String message) {
        reportLogger.report(message);
        //System.out.println(message);
    }

    private void report(String message, Throwable e) {
        //System.out.printf("%s: %s%n", message, e.getMessage());
        reportLogger.report(message, e);
        //System.out.println(message + ": " + e.getMessage());
    }

    public void stop() {
        this.stopped = true;
    }

    private void commit() {
        //nothing
    }
}
