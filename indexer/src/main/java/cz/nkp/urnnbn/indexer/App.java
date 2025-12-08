package cz.nkp.urnnbn.indexer;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Martin Řehánek on 8.2.18.
 */
public class App {
    public static void main(String[] args) {

        IndexerConfig config = new IndexerConfig();
        config.setEsApiBaseUrl("https://es8.dev-service.trinera.cloud");
        config.setEsApiLogin("czidlo_admin");
        config.setEsApiPassword("v/rUJVzWyHcbUp4i2Y");
        config.setDbUrl("jdbc:postgresql://localhost:5432/czidlo_core");
        config.setDbLogin("czidlo");
        config.setDbPassword("czidlo");

        //zbytecny
        config.setEsApiIndexName("INDEX_NAME");
        config.setCzidloApiUseHttps(true);
        config.setCzidloApiBaseUrl("czidlo-api.trinera.cloud/api");

        EsIndexer indexer = new EsIndexer(config, null, new DataProvider() {
            @Override
            public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                return null;
            }

            @Override
            public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                CountryCode.initialize("cz");
                return UrnNbn.valueOf("urn:nbn:cz:pna001-001k7x");
            }
        });
        indexer.indexDocument(1823067);
    }
}
