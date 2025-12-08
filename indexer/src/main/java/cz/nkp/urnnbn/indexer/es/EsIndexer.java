package cz.nkp.urnnbn.indexer.es;

import cz.nkp.urnnbn.apiClient.v5.CzidloApiConnector;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.Counters;
import cz.nkp.urnnbn.indexer.DataProvider;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import cz.nkp.urnnbn.indexer.ProgressListener;
import org.apache.solr.common.SolrException;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

public class EsIndexer {

    private static final Logger logger = Logger.getLogger(EsIndexer.class.getName());

    private final DataProvider dataProvider;
    private ProgressListener progressListener;

    //helpers
    private CzidloApiConnector czidloApiConnector = null;
    private EsConnector esConnector = null;
    private String dbUrl = null;
    private String dbLogin = null;
    private String dbPassword = null;

    public EsIndexer(IndexerConfig config, OutputStream reportLoggerStream, DataProvider dataProvider) {
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
    }

    public void close() {
        //reportLogger.close();
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
            report(" processing " + urnNbn);
            try {
                esConnector.indexDocument(ddInternalId, dbUrl, dbLogin, dbPassword);
                /*String ddCzidloJson = czidloApiConnector.getDigitalDocumentByInternalIdJson(ddInternalId, true);
                if (ddCzidloJson == null) {
                    report(" digital document's json record not found, ignoring");
                    counters.incrementErrors();
                } else {
                    report(" converting & indexing");
                    esConnector.indexJsonString(ddCzidloJson);
                    report(" indexed");
                    counters.incrementIndexed();
                }*/
            } /*catch (CzidloApiErrorException e) {
                counters.incrementErrors();
                report(" CZIDLO API error", e);
            } */ catch (IOException e) {
                counters.incrementErrors();
                report(" I/O error", e);
            } catch (SolrException e) {
                counters.incrementErrors();
                report(" Solr error", e);
            } catch (SQLException e) {
                counters.incrementErrors();
                report(" SQL error", e);
            }
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    private void report(String message) {
        //reportLogger.report(message);
        System.out.println(message);
    }

    private void report(String message, Throwable e) {
        System.out.printf("%s: %s%n", message, e.getMessage());
        //reportLogger.report(message, e);
    }

}
