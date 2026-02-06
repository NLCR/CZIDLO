package cz.nkp.urnnbn.indexer.es;

import com.zaxxer.hikari.HikariDataSource;
import cz.nkp.urnnbn.indexer.DataProvider;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import cz.nkp.urnnbn.indexer.ProgressListener;
import cz.nkp.urnnbn.indexer.ReportLogger;

import java.io.OutputStream;
import java.util.logging.Logger;

public abstract class EsIndexerAbstract implements EsIndexer {

    private static final Logger logger = Logger.getLogger(EsIndexerAbstract.class.getName());

    //status info
    protected boolean stopped = false;
    protected ProgressListener progressListener;
    protected long initTime;

    //helpers
    protected EsConnector esConnector = null;
    protected HikariDataSource dataSource;
    protected final ReportLogger reportLogger;
    protected final IndexerConfig config;
    protected final DataProvider dataProvider;

    public EsIndexerAbstract(IndexerConfig config, OutputStream reportLoggerStream, DataProvider dataProvider) {
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

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    protected String formatTime(long millis) {
        long hours = millis / (60 * 60 * 1000);
        long minutes = millis / (60 * 1000) - hours * 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    protected void report(String message) {
        reportLogger.report(message);
        //System.out.println(message);
    }

    protected void report(String message, Throwable e) {
        //System.out.printf("%s: %s%n", message, e.getMessage());
        reportLogger.report(message, e);
        //System.out.println(message + ": " + e.getMessage());
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    @Override
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

}
