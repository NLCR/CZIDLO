package cz.nkp.urnnbn.indexer.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

public class EsConnector {

    private static final Logger log = LoggerFactory.getLogger(EsConnector.class);
    private final ElasticsearchClient esClient;
    private final String index;

    public EsConnector(String baseurl, String login, String password, String index) {
        this.esClient = initEsClient(baseurl, login, password);
        this.index = index;
        /*try {
            esClient.ping();
        } catch (IOException e) {
            throw new RuntimeException("Could not reach elastic", e);
        }*/
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

        // žádný Jackson, žádný Mapping.OBJECT_MAPPER
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }

    public void indexJsonString(String jsonString) throws IOException {
        //see https://czidlo-api.trinera.cloud/api/v5/digitalDocuments/id/1823067?format=json&digitalInstances=true
        //System.out.println(jsonString);
        JsonObject forIndexing = convertJson(jsonString);
        //System.out.println(forIndexing);
        if (forIndexing == null) {
            System.out.println("Converted JSON is null, skipping indexing.");
            return;
        } else if (!forIndexing.containsKey("id")) {
            System.out.println("Converted JSON does not contain 'id', skipping indexing.");
            return;
        }
        //System.out.println(forIndexing.toString());
        esClient.index(i -> i
                .index(index)
                .id("dd_" + forIndexing.getInt("id"))
                .withJson(new StringReader(forIndexing.toString()))
        );
    }

    private JsonObject convertJson(String jsonString) {
        //System.out.println(jsonString);
        JsonObject jsonIn;
        try (JsonReader r = Json.createReader(new StringReader(jsonString))) {
            jsonIn = r.readObject();
        }
        JsonObjectBuilder jsonOut = Json.createObjectBuilder();
        if (jsonIn.containsKey("digitalDocument")) {
            JsonObject dd = jsonIn.getJsonObject("digitalDocument");
            if (dd.containsKey("id")) {
                jsonOut.add("id", dd.getJsonNumber("id").longValue());
            }
            if (dd.containsKey("urnNbn")) {
                String urnNbn = dd.getString("urnNbn");
                jsonOut.add("urnnbn", urnNbn);
                UrnNbn urn = UrnNbn.valueOf(urnNbn);
                jsonOut.add("registrarcode", urn.getRegistrarCode().toString());
                jsonOut.add("documentcode", urn.getDocumentCode());
            }
            jsonOut.add("active", true); //TODO
            if (dd.containsKey("monograph")) {
                jsonOut.add("entitytype", "MONOGRAPH");
                appendTitleInfo(jsonOut, dd.getJsonObject("monograph"));
            } else if (dd.containsKey("monographVolume")) {
                jsonOut.add("entitytype", "MONOGRAPH_VOLUME");
                appendTitleInfo(jsonOut, dd.getJsonObject("monographVolume"));
            } else if (dd.containsKey("periodical")) {
                jsonOut.add("entitytype", "PERIODICAL");
                appendTitleInfo(jsonOut, dd.getJsonObject("periodical"));
            } else if (dd.containsKey("periodicalVolume")) {
                jsonOut.add("entitytype", "PERIODICAL_VOLUME");
                appendTitleInfo(jsonOut, dd.getJsonObject("periodicalVolume"));
            } else if (dd.containsKey("periodicalIssue")) {
                jsonOut.add("entitytype", "PERIODICAL_ISSUE");
                appendTitleInfo(jsonOut, dd.getJsonObject("periodicalIssue"));
            } else if (dd.containsKey("analytical")) {
                jsonOut.add("entitytype", "ANALYTICAL");
                appendTitleInfo(jsonOut, dd.getJsonObject("analytical"));
            } else if (dd.containsKey("thesis")) {
                jsonOut.add("entitytype", "THESIS");
                appendTitleInfo(jsonOut, dd.getJsonObject("thesis"));
            } else if (dd.containsKey("soundRecording")) {
                jsonOut.add("entitytype", "SOUND_RECORDING");
                appendTitleInfo(jsonOut, dd.getJsonObject("soundRecording"));
            } else if (dd.containsKey("otherEntity")) {
                jsonOut.add("entitytype", "OTHER_ENTITY");
                appendTitleInfo(jsonOut, dd.getJsonObject("otherEntity"));
            }
            if (dd.containsKey("ccnb")) {
                jsonOut.add("ccnb", dd.getString("ccnb"));
            }
            if (dd.containsKey("isbn")) {
                jsonOut.add("isbn", dd.getString("isbn"));
            }
            if (dd.containsKey("issn")) {
                jsonOut.add("issn", dd.getString("issn"));
            }
            if (dd.containsKey("otherId")) {
                jsonOut.add("otherId", dd.getString("otherId"));
            }
        }
        return jsonOut.build();
    }

    private void appendTitleInfo(JsonObjectBuilder jsonOut, JsonObject entity) {
        if (entity != null && entity.containsKey("titleInfo")) {
            JsonObject titleInfo = entity.getJsonObject("titleInfo");
            //for each key in titleInfo, add to jsonOut
            for (String key : titleInfo.keySet()) {
                jsonOut.add(key.toLowerCase(), titleInfo.getString(key));
            }
        }
    }
}


