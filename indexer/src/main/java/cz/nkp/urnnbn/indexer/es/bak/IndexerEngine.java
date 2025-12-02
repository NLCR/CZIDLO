package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import cz.nkp.urnnbn.indexer.solr.SolrIndexer;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class IndexerEngine {

    private static final Logger logger = Logger.getLogger(IndexerEngine.class.getName());

    private final ElasticsearchClient esClient;

    public IndexerEngine(String baseUrl, String login, String password) throws IOException {
        logger.info("Initializing Elasticsearch client for URL: " + baseUrl);
        this.esClient = initEsClient(baseUrl, login, password);
        try {
            esClient.ping();
        } catch (IOException e) {
            throw new IOException("Could not reach elastic", e);
        }
    }

    public void close() {
        try {
            esClient._transport().close();
        } catch (IOException e) {
            logger.warning("Failed to close Elasticsearch client: " + e.getMessage());
        }
    }

    private ElasticsearchClient initEsClient(String baseUrl, String login, String password) {
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        if (login != null && !login.isEmpty()) {
            credsProv.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(login, password)
            );
        }

        RestClient restClient = RestClient.builder(HttpHost.create(baseUrl))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credsProv)
                ).build();

        ElasticsearchTransport transport = new RestClientTransport(restClient,
                new JacksonJsonpMapper(Mapping.OBJECT_MAPPER)
        );

        return new ElasticsearchClient(transport);
    }

    public void indexDocument(String indexName, Object document) {
        try {
            esClient.index(idx -> idx
                    .index(indexName)
                    .id(UUID.randomUUID().toString())
                    .document(document)
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to index document to Elasticsearch: " + e.getMessage());
            throw new RuntimeException("Failed to index document to Elasticsearch", e);
        }
    }

    public void indexBatch(String indexName, List<Object> batch) {
        if (batch.isEmpty()) return;
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Object document : batch) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(indexName)
                            .id(UUID.randomUUID().toString())
                            .document(document)
                    )
            );
        }

        try {
            BulkResponse result = esClient.bulk(br.build());
            if (result.errors()) {
                System.err.println("Batch had errors:");
                result.items().stream()
                        .filter(item -> item.error() != null)
                        .forEach(item -> System.err.println(" - " + item.error().reason()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send batch to Elasticsearch: " + e.getMessage());
            throw new RuntimeException("Failed to send batch to Elasticsearch", e);
        }
    }
}
