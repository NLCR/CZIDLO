package cz.nkp.urnnbn.indexer.es.multi;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.nkp.urnnbn.indexer.es.Config;
import cz.nkp.urnnbn.indexer.es.Utils;
import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiMain {

    private static final int LOG_EVERY_N_RECORDS = 10000;

    //indexy hardcoded jen pro testování
    private static final String INDEX_SEARCH = "czidlo_multimain_search_1";
    private static final String INDEX_RESOLVE = "czidlo_multimain_resolve_1";
    private static final String INDEX_ASSIGN = "czidlo_multimain_assign_1";

    public static void main(String[] args) throws SQLException {
        Properties props = Utils.loadProperties();

        ObjectMapper mapper = Config.getObjectMapper();

        ElasticsearchClient elasticClient = Utils.createElasticClient(props, Config.getObjectMapper());

        try (Connection conn = Utils.createConnection(props)) {

            if (true) { // set to false to skip Searching migration
                System.out.println();
                System.out.println("-------------------");
                System.out.println("Migrating Searching");
                System.out.println("-------------------");
                AtomicInteger counterSearching = new AtomicInteger();
                DataMigrator searchingMigrator = new DataMigrator(conn, mapper, (batch) -> {
                    indexBatch(elasticClient, INDEX_SEARCH, batch);
                    counterSearching.addAndGet(batch.size());
                    if (counterSearching.get() % LOG_EVERY_N_RECORDS < batch.size()) {
                        System.out.println("Indexed total " + counterSearching.get() + " Searching items so far");
                    }
                });
                searchingMigrator.migrateSearching();
                System.out.println("Total Searching items migrated: " + counterSearching.get());
            }

            if (true) { // set to false to skip Assigning migration
                System.out.println();
                System.out.println("-------------------");
                System.out.println("Migrating Assigning");
                System.out.println("-------------------");
                AtomicInteger counterAssigning = new AtomicInteger();
                DataMigrator assigningMigrator = new DataMigrator(conn, mapper, (batch) -> {
                    indexBatch(elasticClient, INDEX_ASSIGN, batch);
                    counterAssigning.addAndGet(batch.size());
                    if (counterAssigning.get() % LOG_EVERY_N_RECORDS < batch.size()) {
                        System.out.println("Indexed total " + counterAssigning.get() + " Assigning items so far");
                    }
                });
                assigningMigrator.migrateAssigning();
                System.out.println("Total Assigning items migrated: " + counterAssigning.get());
            }

            if (true) { // set to false to skip Resolving migration
                System.out.println();
                System.out.println("-------------------");
                System.out.println("Migrating Resolving");
                System.out.println("-------------------");
                AtomicInteger counterResolving = new AtomicInteger();
                DataMigrator resolvingMigrator = new DataMigrator(conn, mapper, (batch) -> {
                    indexBatch(elasticClient, INDEX_RESOLVE, batch);
                    counterResolving.addAndGet(batch.size());
                    if (counterResolving.get() % LOG_EVERY_N_RECORDS < batch.size()) {
                        System.out.println("Indexed total " + counterResolving.get() + " Resolving items so far");
                    }
                });
                resolvingMigrator.migrateResolving();
                System.out.println("Total Resolving items migrated: " + counterResolving.get());
            }

        } finally { // ensure elastic client is closed, otherwise app may hang
            Utils.stopElasticsearchClient(elasticClient);
            System.out.println("Finished migrating");
        }
    }


    private static int indexBatch(ElasticsearchClient client, String indexName, List<DomainIdx> batch) {
        if (Config.DISABLE_INDEXING) {
            //System.out.println("Indexing is disabled, skipping indexing batch of " + batch.size() + " items to index " + indexName);
            return batch.size();
        }

        //System.out.println("Indexing batch of " + batch.size() + " items into index " + indexName);
        if (batch.isEmpty()) return 0;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (DomainIdx document : batch) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(indexName)
                            .id(document.getId())
                            .document(document)
                    )
            );
        }

        try {
            BulkRequest request = br.build();
            //int operations = request.operations().size();
            //System.out.println("Indexing batch of " + operations + " documents to index " + indexName);
            BulkResponse result = client.bulk(request);
            if (result.errors()) {
                System.err.println("Batch had errors:");
                result.items().stream()
                        .filter(item -> item.error() != null)
                        .forEach(item -> System.err.println(" - " + item.error().reason()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return batch.size();
    }

}
