package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class DataMigrator {

    private static int BATCH_SIZE = 1000;
    private static final ConversionType type = new Resolving(); // change class for different mapping

    public static void main(String[] args) {
        IndexerEngine indexerEngine = null;
        try {
            Properties props = loadProperties();

            //init and check ES connection
            String indexName = props.getProperty("es.index.name");
            //init indexer
            indexerEngine = new IndexerEngine(props.getProperty("es.url"),
                    props.getProperty("es.username"),
                    props.getProperty("es.password"));

            //define batch processor
            System.out.println("h1");
            IndexerEngine finalIndexerEngine = indexerEngine;
            Consumer<List<Object>> batchProcessor = (batch) -> {
                System.out.println("Received batch of " + batch.size() + " records. Indexing...");
                finalIndexerEngine.indexBatch(indexName, batch);
            };
            System.out.println("h2");

            System.out.println("--- STARTING MIGRATION (STREAMING) ---");

            //start processing
            processFromPostgres(props, batchProcessor);

            System.out.println("--- MIGRATION COMPLETE ---");
        } catch (IOException e) {
            System.err.println("Failed to initialize indexer engine or load properties.");
            if (indexerEngine != null) {
                indexerEngine.close();
            }
        }
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
                System.out.println(type.query());
                ResultSet rs = stmt.executeQuery(type.query());
                System.out.println("Finished executing SQL statement.");

                List<Object> batch = new ArrayList<>();
                while (rs.next()) {
                    String json = rs.getString("resulting_json");
                    Object mapped = null;
                    try {
                        mapped = Mapping.OBJECT_MAPPER.readValue(json, type.getClass());
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


  /*  private static ElasticsearchClient createElasticClient(Properties props) {
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
    }*/

    private static Properties loadProperties() {
        Properties props = new Properties();
        File hardcoderConfigFile = new File("/Users/martinrehanek/TrineraProjects/Czidlo/CZIDLO/indexer/src/main/resources/indexer-es.properties");
        //try (InputStream input = DataMigrator.class.getClassLoader().getResourceAsStream("config.properties")) {
        System.out.println("Loading properties from " + hardcoderConfigFile.getAbsolutePath());
        try (InputStream input = new FileInputStream(hardcoderConfigFile)) {
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