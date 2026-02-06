package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nkp.urnnbn.indexer.ReportLogger;
import cz.nkp.urnnbn.indexer.es.single.DdEsConversionResult;
import cz.nkp.urnnbn.indexer.es.single.EsDataProvider;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class EsConnector implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(EsConnector.class);
    private final DataSource dataSource; //database
    private final RestClient restClient;
    private final ElasticsearchTransport transport;
    private final ElasticsearchClient esClient;
    private final String indexSearch;
    private final String indexAssign;
    private final String indexResolve;

    public EsConnector(String baseurl, String login, String password, String indexSearch, String indexAssign, String indexResolve, DataSource dataSource) {
        this.dataSource = dataSource;
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (login != null && !login.isEmpty()) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(login, password));
        }

        this.restClient = RestClient.builder(HttpHost.create(baseurl))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        this.transport = new RestClientTransport(
                this.restClient,
                new JacksonJsonpMapper(Config.getObjectMapper())
        );

        this.esClient = new ElasticsearchClient(this.transport);
        this.indexSearch = indexSearch;
        this.indexAssign = indexAssign;
        this.indexResolve = indexResolve;
    }

    private ElasticsearchClient initEsClient(String baseUrl, String login, String password) {
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        if (login != null && !login.isEmpty()) {
            credsProv.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(login, password)
            );
        }

        //System.out.println("Elasticsearch base URL: " + baseUrl);
        RestClient restClient = RestClient.builder(HttpHost.create(baseUrl))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credsProv)
                )
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper(Config.getObjectMapper())
        );
        return new ElasticsearchClient(transport);
    }

    public void indexDigitalDocument(long ddInternalId, ReportLogger reportLogger) throws IOException, SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ObjectMapper mapper = Config.getObjectMapper();
            EsDataProvider dataProvider = new EsDataProvider(conn, mapper);
            //reportLogger.report("Indexing digital document with internal id: " + ddInternalId);
            DdEsConversionResult conversionResult = dataProvider.convertDigitalDocumentJson(ddInternalId);
            //System.out.println(conversionResult.getSearch());

            //index Search
            if (conversionResult.getSearch() != null) {
                esClient.index(idx -> idx.index(indexSearch)
                        .id(conversionResult.getSearch().getId())
                        .document(conversionResult.getSearch()));
            }
            //index Assigning
            if (conversionResult.getAssignment() != null) {
                esClient.index(idx -> idx.index(indexAssign)
                        .id(conversionResult.getAssignment().getId())
                        .document(conversionResult.getAssignment()));
            }
        }
    }

    public void indexDigitalDocumentWithTiming(long ddInternalId, ReportLogger reportLogger) throws IOException, SQLException {
        long t0 = System.nanoTime();

        try (Connection conn = dataSource.getConnection()) {
            long tConn = System.nanoTime();

            ObjectMapper mapper = Config.getObjectMapper();
            EsDataProvider dataProvider = new EsDataProvider(conn, mapper);

            DdEsConversionResult conversionResult = dataProvider.convertDigitalDocumentJson(ddInternalId);
            long tConv = System.nanoTime();

            if (conversionResult.getSearch() != null) {
                esClient.index(idx -> idx.index(indexSearch)
                        .id(conversionResult.getSearch().getId())
                        .document(conversionResult.getSearch()));
            }
            long tIdx1 = System.nanoTime();

            if (conversionResult.getAssignment() != null) {
                esClient.index(idx -> idx.index(indexAssign)
                        .id(conversionResult.getAssignment().getId())
                        .document(conversionResult.getAssignment()));
            }
            long tIdx2 = System.nanoTime();

            System.out.printf(
                    "dd=%d conn=%dms convert=%dms esSearch=%dms esAssign=%dms total=%dms%n",
                    ddInternalId,
                    (tConn - t0) / 1_000_000,
                    (tConv - tConn) / 1_000_000,
                    (tIdx1 - tConv) / 1_000_000,
                    (tIdx2 - tIdx1) / 1_000_000,
                    (tIdx2 - t0) / 1_000_000
            );
        }
    }

    public void indexResolvation(long resolvingId, ReportLogger reportLogger) throws IOException, SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ObjectMapper mapper = Config.getObjectMapper();
            EsDataProvider dataProvider = new EsDataProvider(conn, mapper);
            DdEsConversionResult conversionResult = dataProvider.convertResolvingJson(resolvingId);

            if (conversionResult.getResolve() != null) {
                esClient.index(idx -> idx.index(indexResolve)
                        .id(conversionResult.getResolve().getId())
                        .document(conversionResult.getResolve()));
            }
        }
    }

    @Override
    public void close() {
        try { //closing transport as well to be sure
            transport.close();
        } catch (Exception e) {
            log.warn("Failed to close Elasticsearch transport", e);
        }
        try {
            restClient.close();
        } catch (IOException e) {
            log.warn("Failed to close Elasticsearch RestClient", e);
        }
    }

    // DB pool zavírá EsIndexer
}


