package cz.nkp.urnnbn.indexer;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Martin Řehánek on 8.2.18.
 */
public class App {
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

        //indexSingleDocument(config, 1823067, "urn:nbn:cz:pna001-001k7x");
        indexDocumentInLoop(config, 1823067, "urn:nbn:cz:pna001-001k7x");
    }

    private static void indexDocumentInLoop(IndexerConfig config, int ddId, String urnNbn) {
        EsIndexer indexer = new EsIndexer(config, null, new DataProvider() {
            @Override
            public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                return null;
            }

            @Override
            public List<ResolvationLog> resolvationLogsByDate(DateTime from, DateTime until) {
                return null;
            }

            @Override
            public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                return UrnNbn.valueOf(urnNbn);
            }
        });

        long start = System.currentTimeMillis();
        int counter = 0;
        int limit = 50;
        CountryCode.initialize("cz");

        while (counter++ < limit) {
            System.out.println("Indexation iteration: " + counter);
            indexer.indexDigitalDocument(ddId);
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("Total time for " + limit + " iterations: " + duration + " ms");
        System.out.println("Average time per iteration: " + (duration / limit) + " ms");
        indexer.close();
    }

    private static void indexSingleDocument(IndexerConfig config, int ddId, String urnNbn) {
        EsIndexer indexer = new EsIndexer(config, null, new DataProvider() {
            @Override
            public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                return null;
            }

            @Override
            public List<ResolvationLog> resolvationLogsByDate(DateTime from, DateTime until) {
                return null;
            }

            @Override
            public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                CountryCode.initialize("cz");
                return UrnNbn.valueOf(urnNbn);
            }
        });
        indexer.indexDigitalDocument(ddId);
        indexer.close();
    }
}
