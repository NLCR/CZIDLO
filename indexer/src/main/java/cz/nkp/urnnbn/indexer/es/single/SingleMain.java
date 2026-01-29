package cz.nkp.urnnbn.indexer.es.single;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        //Long digitalDocumentId = 3120326L;

        //digitalBorn=false
        //Long digitalDocumentId = 3081845L;//urn:nbn:cz:pna001-00cy37 (ma rezolvujici zaznam)
        //Long resolvingId = 1129973L; //urn:nbn:cz:pna001-00cy37 resolved at 2026-01-25 23:06:02.001

        //digitalBorn=true; ccnb, isbn (not in source-document)
        //Long digitalDocumentId = 3120326L;//urn:nbn:cz:aba007-0009a3 (ma rezolvujici zaznam)
        //Long resolvingId = 1129975L; //urn:nbn:cz:aba007-0009a3 resolved at 2026-01-26 15:39:12.077

        //active=false
        //Long digitalDocumentId = 501L;//urn:nbn:cz:aba001-0007l0 (ma rezolvujici zaznam)
        //Long resolvingId = 2259948L; //urn:nbn:cz:aba001-0007l0 resolved at 2026-01-03 14:15:16

        //ccnb, issn (not in source-document)
        //Long digitalDocumentId = 3525443L;//urn:nbn:cz:ope301-00038f, not resolved
        //Long resolvingId = null;

        //Resolving id without document code (old data)
        Long digitalDocumentId = null;
        Long resolvingId = 710L; //aba001 from 2025-08-01 12:00:00 (i.e. something from august 2025)

        Properties props = Utils.loadProperties();
        ObjectMapper mapper = Config.getObjectMapper();

        String indexSearch = "czidlo_singlemain_search_1";
        String indexAssign = "czidlo_singlemain_assign_1";
        String indexResolve = "czidlo_singlemain_resolve_1";

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
            indexDigDocData(elasticClient, conversionResult, indexSearch, indexAssign);
            indexDigDocAccessData(elasticClient, conversionResolvingResult, indexResolve);
        } finally {
            Utils.stopElasticsearchClient(elasticClient);
        }
    }

    private static void indexDigDocData(ElasticsearchClient esClient, DdEsConversionResult conversionResult, String indexSearch, String indexAssign) {
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

    private static void indexDigDocAccessData(ElasticsearchClient esClient, DdEsConversionResult conversionResult, String indexResolve) {
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
