package cz.nkp.urnnbn.indexer.es;

import cz.nkp.urnnbn.apiClient.v5.CzidloApiConnector;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.*;
import org.apache.solr.common.SolrException;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EsIndexer {

    private static final Logger logger = Logger.getLogger(EsIndexer.class.getName());

    private final DataProvider dataProvider;
    private final ReportLogger reportLogger;

    //status info
    private boolean stopped = false;
    private ProgressListener progressListener;
    private long initTime;

    //helpers
    private CzidloApiConnector czidloApiConnector = null;
    private EsConnector esConnector = null;
    private String dbUrl = null;
    private String dbLogin = null;
    private String dbPassword = null;

    public EsIndexer(IndexerConfig config, OutputStream reportLoggerStream, DataProvider dataProvider) {
        long start = System.currentTimeMillis();
        this.reportLogger = new ReportLogger(reportLoggerStream);
        this.dataProvider = dataProvider;
        String baseUrl = config.getEsApiBaseUrl();
        String login = config.getEsApiLogin();
        String password = config.getEsApiPassword();
        String index = config.getEsApiIndexName();

        logger.info("Initializing Elasticsearch client for URL: " + baseUrl);
        this.esConnector = new EsConnector(baseUrl, login, password, index);
        this.czidloApiConnector = new CzidloApiConnector(
                config.getCzidloApiBaseUrl(),
                null,
                config.getCzidloApiUseHttps(),
                false);
        this.dbUrl = config.getDbUrl();
        this.dbLogin = config.getDbLogin();
        this.dbPassword = config.getDbPassword();
        this.initTime = System.currentTimeMillis() - start;
    }

    public void close() {
        if (reportLogger != null) {
            reportLogger.close();
        }
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void indexResolvation(ResolvationLog resolvationLog) {
        try {
            esConnector.indexResolvation(resolvationLog, dbUrl, dbLogin, dbPassword);
        } catch (SQLException e) {
            report(" SQL error", e);
        } catch (IOException e) {
            report(" I/O error", e);
        }
    }

    public void indexDocument(long ddInternalId) {
        indexDocument(ddInternalId, new Counters(1), true);
    }

    private void indexDocument(long ddInternalId, Counters counters, boolean explicitCommit) {
        UrnNbn urnNbn = dataProvider.urnByDigDocId(ddInternalId, false);
        if (urnNbn == null) {
            report(" digital document with id " + ddInternalId + " is missing URN:NBN");
        } else {
            //report(" processing " + urnNbn);
            try {
                esConnector.indexDocument(ddInternalId, dbUrl, dbLogin, dbPassword, reportLogger);
                counters.incrementIndexed();
            } catch (IOException e) {
                counters.incrementErrors();
                report(" I/O error", e);
            } catch (SolrException e) {
                counters.incrementErrors();
                report(" Solr error", e);
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

    public void indexDocuments(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        report("Indexing documents from " + from.toString() + " to " + to.toString());
        List<DigitalDocument> digitalDocuments = dataProvider.digDocsByModificationDate(from, to);
        Counters counters = new Counters(digitalDocuments.size());
        report("Processing " + counters.getFound() + " records");
        Integer limit = null; // for testing, set to null for production
        int iterationCount = 0;
        report("==============================");
        for (DigitalDocument doc : digitalDocuments) {
            if (stopped) {
                report(" stopped ");
                break;
            }
            if (limit != null && iterationCount++ >= limit) {
                report(" limit of " + limit + " reached, stopping (for testing purposes) ");
                break;
            }
            indexDocument(doc.getId(), counters, false);
        }
        commit(); //one explicit commit at the very end
        report(" ");

        report("Summary");
        report("=====================================================");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization duration: " + formatTime(initTime));
        report(" records processing duration: " + formatTime(System.currentTimeMillis() - start));
        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
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
        /*try {
            solrConnector.commit();
        } catch (SolrServerException | IOException e) {
            report(" Solr server error while commiting", e);
        }*/
    }
}
