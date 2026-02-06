package cz.nkp.urnnbn.processmanager;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.indexer.DataProvider;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import cz.nkp.urnnbn.indexer.es.EsIndexerBatching;
import cz.nkp.urnnbn.services.Services;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class IndexerJobTest {

    public static void main(String[] args) throws IOException {

        IndexerConfig config = new IndexerConfig();
        Properties props = new Properties();
        props.load(new FileInputStream("/Users/martinrehanek/TrineraProjects/Czidlo/CZIDLO/.local/properties/indexer-test.properties"));

        // Indexer configuration
        config.setEsApiBaseUrl(props.getProperty("es.baseUrl"));
        config.setEsApiLogin(props.getProperty("es.login"));
        config.setEsApiPassword(props.getProperty("es.password"));
        config.setEsApiIndexSearchName(props.getProperty("es.index_search"));
        config.setEsApiIndexAssignName(props.getProperty("es.index_assign"));
        config.setEsApiIndexResolveName(props.getProperty("es.index_resolve"));

        // CZIDLO database connection
        config.setDbUrl(props.getProperty("db.url"));
        config.setDbLogin(props.getProperty("db.login"));
        config.setDbPassword(props.getProperty("db.password"));

        Services.init(initDatabaseConnector(config), null);
        indexDocumentsInDateRange(config, DateTime.parse("2025-01-01"), DateTime.parse("2025-01-08"));
        indexResolvationLogsInDateRange(config, DateTime.parse("2021-03-01"), DateTime.parse("2021-03-02"));
    }

    private static void indexResolvationLogsInDateRange(IndexerConfig config, DateTime fromInclusive, DateTime untilExclusive) {
        EsIndexer indexer = new EsIndexerBatching(config, System.out, initDataProvider());
        indexer.indexResolvationLogs(fromInclusive, untilExclusive);
        indexer.close();
    }

    private static DatabaseConnector initDatabaseConnector(IndexerConfig config) {
        return new PostgresPooledConnector(config.getDbUrl(), config.getDbLogin(), config.getDbPassword());
    }

    private static void indexDocumentsInDateRange(IndexerConfig config, DateTime fromInclusive, DateTime untilExclusive) {
        EsIndexer indexer = new EsIndexerBatching(config, System.out, initDataProvider());
        indexer.indexDigitalDocuments(fromInclusive, untilExclusive);
        indexer.close();
    }

    private static DataProvider initDataProvider() {
        return new DataProvider() {
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
        };
    }
}
