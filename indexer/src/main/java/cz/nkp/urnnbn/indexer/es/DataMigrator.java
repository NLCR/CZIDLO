package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

public class DataMigrator {

    private static int BATCH_SIZE = 1000;
    private static final ConversionType type = new Resolving(); // change class for different mapping

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void main(String[] args) {
        Properties props = loadProperties();

        ElasticsearchClient esClient = createElasticClient(props);
        String indexName = props.getProperty("es.index.name");
        try {
            esClient.ping();
        } catch (IOException e) {
            throw new RuntimeException("Could not reach elastic", e);
        }

        Consumer<List<Object>> batchProcessor = (batch) -> {
            System.out.println("Received batch of " + batch.size() + " records. Indexing...");
            indexBatch(esClient, indexName, batch);
        };

        System.out.println("--- STARTING MIGRATION (STREAMING) ---");

        processFromPostgres(props, batchProcessor);
        //processFromSolr(props, batchProcessor);

        System.out.println("--- MIGRATION COMPLETE ---");
    }

    private static void processFromPostgres(Properties props, Consumer<List<Object>> processor) {
        try (Connection conn = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"))) {

            conn.setAutoCommit(false);

            System.out.println("Connected to PostgreSQL.");

            try (Statement stmt = conn.createStatement()) {

                //joins all the tables according to their keys (trust)
                System.out.println("Executing SQL statement.");
                ResultSet rs = stmt.executeQuery(type.query());
                System.out.println("Finished executing SQL statement.");

                List<Object> batch = new ArrayList<>();
                while (rs.next()) {
                    String json = rs.getString("resulting_json");
                    Object mapped = null;
                    try {
                        mapped = OBJECT_MAPPER.readValue(json, type.getClass());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    if (mapped instanceof Resolving) {
                        BATCH_SIZE = 1;
                        for (int i = 0; i < ((Resolving) mapped).sum; i++) {
                            Resolving.Item resolving = new Resolving.Item();
                            resolving.registrarcode = ((Resolving) mapped).registrarcode;
                            resolving.registrarname = ((Resolving) mapped).registrarname;
                            resolving.resolved = LocalDateTime.of(((Resolving) mapped).year, ((Resolving) mapped).month + 1, 1, 11, 0, 0);
                            batch.add(resolving);
                        }
                    } else {
                        batch.add(mapped);
                    }

                    if (batch.size() >= BATCH_SIZE) {
                        processor.accept(batch);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    processor.accept(batch);
                }
            }


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


    private static void indexBatch(ElasticsearchClient client, String indexName, List<Object> batch) {
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
            BulkResponse result = client.bulk(br.build());
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


    private static ElasticsearchClient createElasticClient(Properties props) {
        String serverUrl = props.getProperty("es.url");
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        String user = props.getProperty("es.username");
        if (user != null && !user.isEmpty()) {
            credsProv.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(user, props.getProperty("es.password"))
            );
        }

        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credsProv)
                ).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(OBJECT_MAPPER)
        );

        return new ElasticsearchClient(transport);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DataMigrator.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                System.exit(1);
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

}