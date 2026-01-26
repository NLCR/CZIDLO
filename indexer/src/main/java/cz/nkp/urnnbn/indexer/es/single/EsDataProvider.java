package cz.nkp.urnnbn.indexer.es.single;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nkp.urnnbn.indexer.es.domain.assigning.Assigning;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningIdx;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningQueryBuilder;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningValidator;
import cz.nkp.urnnbn.indexer.es.domain.resolving.Resolving;
import cz.nkp.urnnbn.indexer.es.domain.resolving.ResolvingIdx;
import cz.nkp.urnnbn.indexer.es.domain.resolving.ResolvingQueryBuilder;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchQueryBuilder;
import cz.nkp.urnnbn.indexer.es.domain.searching.Searching;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingIdx;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EsDataProvider {

    private final Logger log = LoggerFactory.getLogger(EsDataProvider.class);

    private final SearchingValidator searchingValidator = new SearchingValidator();
    private final AssigningValidator assigningValidator = new AssigningValidator();

    private final Connection conn;
    private final ObjectMapper mapper;


    public EsDataProvider(Connection conn, ObjectMapper mapper) {
        this.conn = conn;
        this.mapper = mapper;
    }

    public DdEsConversionResult convertDigitalDocumentJson(Long ddId) {
        try {
            return new DdEsConversionResult(
                    convertSearching(ddId),
                    convertAssigning(ddId)
            );
        } catch (SQLException | JsonProcessingException | IllegalArgumentException e) {
            throw new RuntimeException("Failed conversion for ddId: " + ddId, e);
        }
    }

    public DdEsConversionResult convertResolvingJson(Long urId) {
        if (urId == null) {
            log.warn("urId is null, cannot convert ResolvingIdx");
            return new DdEsConversionResult(null);
        }
        try {
            return new DdEsConversionResult(
                    convertResolving(urId)
            );
        } catch (SQLException | JsonProcessingException | IllegalArgumentException e) {
            throw new RuntimeException("Failed conversion for urId: " + urId, e);
        }
    }

    private SearchingIdx convertSearching(Long ddId) throws SQLException, JsonProcessingException, IllegalArgumentException {
        String query = new SearchQueryBuilder()
                .withAlias("resulting_json")
                .where("dd.id = ?")
                .build();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, ddId);
            ResultSet resultSet = stmt.executeQuery();

            if (!resultSet.next()) {
                //throw new RuntimeException("No result found for id " + ddId);
                return null;
            }

            String json = resultSet.getString("resulting_json");
            Searching searching = mapper.readValue(json, Searching.class);

            searchingValidator.validate(searching);

            return SearchingIdx.fromDb(searching);
        }
    }

    private AssigningIdx convertAssigning(Long ddId) throws SQLException, JsonProcessingException, IllegalArgumentException {
        String query = new AssigningQueryBuilder()
                .withAlias("resulting_json")
                .where("dd.id = ?")
                .build();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, ddId);
            ResultSet resultSet = stmt.executeQuery();

            if (!resultSet.next()) {
                //throw new RuntimeException("No result found for id " + ddId);
                return null;
            }

            String json = resultSet.getString("resulting_json");
            Assigning assigning = mapper.readValue(json, Assigning.class);

            assigningValidator.validate(assigning);

            return AssigningIdx.fromDb(assigning);
        }
    }

    private ResolvingIdx convertResolving(Long urID) throws SQLException, JsonProcessingException, IllegalArgumentException {
        String query = new ResolvingQueryBuilder()
                .withAlias("resulting_json")
                .where("ur.id = ?")
                .build();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, urID);
            ResultSet resultSet = stmt.executeQuery();

            if (!resultSet.next()) {
                //throw new RuntimeException("No result found for id " + urId);
                return null;
            }

            String json = resultSet.getString("resulting_json");
            System.out.println(json);
            Resolving resolving = mapper.readValue(json, Resolving.class);

            //validate

            return ResolvingIdx.fromDb(resolving);
        }
    }

}
