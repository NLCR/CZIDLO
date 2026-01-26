package cz.nkp.urnnbn.indexer.es.single;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.nkp.urnnbn.indexer.es.Config;
import cz.nkp.urnnbn.indexer.es.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SingleMain {
    public static void main(String[] args) throws SQLException {
        //Long digitalDocumentId = 3764488L; // change to desired ID
        //Long digitalDocumentId = 3766263L; // crashes SearchingValidator with NullPointerException
        //Long digitalDocumentId = 2719329L; //má registrar-scope id (K4_pid:CAEB0C3C-E4E8-42E2-8730-4C15A61529D4) urn:nbn:cz:vkol-009vgk
        //Long digitalDocumentId = 1254004L; // má registrar-scope id (uuid:8b489e90-4860-11e4-8113-005056827e52, OAI_Adapter:uuid:8b489e90-4860-11e4-8113-005056827e52) urn:nbn:cz:mzk-0039uq
        //Long digitalDocumentId = 8L; //ma publishera urn:nbn:cz:tst02-000004
        Long digitalDocumentId = 3120326L;
        Long resolvingId = 2259946L;

        Properties props = Utils.loadProperties();
        ObjectMapper mapper = Config.getObjectMapper();

        String indexSearch = "czidlo_multimain_search_1";
        String indexAssign = "czidlo_multimain_assign_1";
        String indexResolve = "czidlo_multimain_resolve_1";

        ElasticsearchClient elasticClient = Utils.createElasticClient(props, mapper);
        try (Connection conn = Utils.createConnection(props)) {
            EsDataProvider dataProvider = new EsDataProvider(conn, mapper);
            System.out.println("Starting data provider...");
            DdEsConversionResult conversionResult = dataProvider.convertDigitalDocumentJson(digitalDocumentId);
            DdEsConversionResult conversionResolvingResult = dataProvider.convertResolvingJson(resolvingId);
            System.out.println("Finished data provider.");
            //System.out.println(conversionResult.getSearch());
            //System.out.println(conversionResult.getAssignment());
            //System.out.println(conversionResolvingResult.getResolve());
            indexData(elasticClient, conversionResult, indexSearch, indexAssign);
            indexData(elasticClient, conversionResolvingResult, indexResolve);
        } finally {
            Utils.stopElasticsearchClient(elasticClient);
        }
    }

    //TODO: implement indexAccessLogs

    //TODO: rename to indexDigitalDocs
    private static void indexData(ElasticsearchClient esClient, DdEsConversionResult conversionResult, String indexSearch, String indexAssign) {
        try {
            if (Config.DISABLE_INDEXING) {
                System.out.println("Indexing is disabled, skipping indexing step.");
                return;
            }

            if (conversionResult.getSearch() != null) {
                esClient.index(idx -> idx
                        .index(indexSearch)
                        .id(conversionResult.getSearch().getId())
                        .document(conversionResult.getSearch())
                );
            }

            if (conversionResult.getAssignment() != null) {
                esClient.index(idx -> idx
                        .index(indexAssign)
                        .id(conversionResult.getAssignment().getId())
                        .document(conversionResult.getAssignment())
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void indexData(ElasticsearchClient esClient, DdEsConversionResult conversionResult, String indexResolve) {
        try {
            if (Config.DISABLE_INDEXING) {
                System.out.println("Indexing is disabled, skipping indexing step.");
                return;
            }

            if (conversionResult.getResolve() != null) {
                esClient.index(idx -> idx
                        .index(indexResolve)
                        .id(conversionResult.getResolve().getId())
                        .document(conversionResult.getResolve())
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
