package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Utils {

    @Deprecated
    public static Connection createConnection(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    @Deprecated
    public static Connection createConnection(Properties props) throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }

    public static HikariDataSource createPooledDataSource(String jdbcUrl, String user, String pass) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        // rozumné defaulty pro job
        cfg.setMaximumPoolSize(8);          // podle paralelismu jobu
        cfg.setMinimumIdle(0);
        cfg.setConnectionTimeout(10_000);   // čekání na connection z poolu
        cfg.setValidationTimeout(5_000);

        // držet connection čerstvé: velmi důležité pro dlouhé joby
        cfg.setMaxLifetime(30 * 60_000L);   // 30 min (menší než DB/LB timeout)
        cfg.setIdleTimeout(5 * 60_000L);    // 5 min
        cfg.setKeepaliveTime(60_000L);      // 60s ping (pomáhá proti idle resetům)

        // driver hint (nepovinné, ale někdy pomůže)
        cfg.setPoolName("czidlo-indexer-dbpool");

        return new HikariDataSource(cfg);
    }

    public static Connection getConnection(DataSource ds) throws SQLException {
        return ds.getConnection();
    }

    public static ElasticsearchClient createElasticClient(Properties props, ObjectMapper mapper) {
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
                restClient, new JacksonJsonpMapper(mapper)
        );

        return new ElasticsearchClient(transport);
    }

    public static void stopElasticsearchClient(ElasticsearchClient esClient) {
        try {
            esClient._transport().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties loadProperties() {
        /*Properties props = new Properties();
        File propFile = new File("/Users/martinrehanek/.czidlo/indexer-test.properties");
        try {
            props.load(java.nio.file.Files.newInputStream(propFile.toPath()));
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        Properties props = new Properties();
        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream("config.properties")) {
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
