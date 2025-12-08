package cz.nkp.urnnbn.indexer.es.multi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;
import cz.nkp.urnnbn.indexer.es.domain.assigning.Assigning;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningIdx;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningQueryBuilder;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningValidator;
import cz.nkp.urnnbn.indexer.es.domain.resolving.Resolving;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchQueryBuilder;
import cz.nkp.urnnbn.indexer.es.domain.searching.Searching;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingIdx;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataMigrator {

    private static final Integer DB_READ_OFFSET = null; //from which record to start reading from DB; set to null to start from beginning (production)
    private static final Integer DB_READ_LIMIT = null; //how many records to read from DB; set to null to read all (production)
    private static final Integer DB_FETCH_SIZE = 1000; //how many records to fetch from DB in one roundtrip (db driver side buffer size, otherwise whole DB is loaded into memory)
    private static final int MAX_INDEXING_BATCH_SIZE = 1000; //how many records to index in one batch

    private final SearchingValidator searchingValidator = new SearchingValidator();
    private final AssigningValidator assigningValidator = new AssigningValidator();

    private final Connection conn;
    private final ObjectMapper mapper;
    private final Consumer<List<DomainIdx>> batchProcessor;

    public DataMigrator(Connection conn, ObjectMapper mapper, Consumer<List<DomainIdx>> batchProcessor) {
        this.conn = conn;
        this.mapper = mapper;
        this.batchProcessor = batchProcessor;
    }

    public void migrateSearching() {
        System.out.println("Starting migrating Searching...");
        if (DB_READ_OFFSET != null) {
            System.out.println("Omitting first " + DB_READ_OFFSET + " records read from DB, waiting to get there ...");
        }
        try {
            conn.setAutoCommit(false);

            String query = new SearchQueryBuilder()
                    .withAlias("resulting_json")
                    .limit(DB_READ_LIMIT)
                    .offset(DB_READ_OFFSET)
                    .build();

            //System.out.println("Executing query: " + query);

            try (Statement stmt = conn.createStatement()) {
                stmt.setFetchSize(DB_FETCH_SIZE == null ? 0 : DB_FETCH_SIZE);
                ResultSet resultSet = stmt.executeQuery(query);

                List<DomainIdx> batch = new ArrayList<>();
                while (resultSet.next()) {
                    String json = resultSet.getString("resulting_json");
                    try {
                        Searching searching = mapper.readValue(json, Searching.class);

                        searchingValidator.validate(searching);

                        SearchingIdx searchingIdx = SearchingIdx.fromDb(searching);
                        batch.add(searchingIdx);
                    } catch (JsonProcessingException | IllegalArgumentException e) {
                        System.out.println("Failed to parse json: " + json + " --- " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }

                    //System.out.println("batch size: " + batch.size());
                    if (batch.size() >= MAX_INDEXING_BATCH_SIZE) {
                        batchProcessor.accept(batch);
                        batch.clear();
                    }
                }
                // don't forget remaining to process remaining items in batch!
                if (!batch.isEmpty()) {
                    batchProcessor.accept(batch);
                    batch.clear();
                }
            }

            conn.setAutoCommit(true);
            System.out.println("Finished migrating Searching.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void migrateAssigning() {
        System.out.println("Starting migrating Assigning...");
        if (DB_READ_OFFSET != null) {
            System.out.println("Omitting first " + DB_READ_OFFSET + " records read from DB, waiting to get there ...");
        }
        try {
            conn.setAutoCommit(false);

            String query = new AssigningQueryBuilder()
                    .withAlias("resulting_json")
                    .limit(DB_READ_LIMIT)
                    .offset(DB_READ_OFFSET)
                    .build();

            try (Statement stmt = conn.createStatement()) {
                stmt.setFetchSize(DB_FETCH_SIZE == null ? 0 : DB_FETCH_SIZE);
                ResultSet resultSet = stmt.executeQuery(query);

                List<DomainIdx> batch = new ArrayList<>();
                while (resultSet.next()) {
                    String json = resultSet.getString("resulting_json");
                    try {
                        Assigning assigning = mapper.readValue(json, Assigning.class);

                        assigningValidator.validate(assigning);

                        AssigningIdx assigningIdx = AssigningIdx.fromDb(assigning);
                        batch.add(assigningIdx);
                    } catch (JsonProcessingException | IllegalArgumentException e) {
                        System.out.println("Failed to parse json: " + json + " --- " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }

                    if (batch.size() >= MAX_INDEXING_BATCH_SIZE) {
                        batchProcessor.accept(batch);
                        batch.clear();
                    }
                }
                // don't forget remaining to process remaining items in batch!
                if (!batch.isEmpty()) {
                    batchProcessor.accept(batch);
                    batch.clear();
                }
            }

            conn.setAutoCommit(true);
            System.out.println("Finished migrating Assigning.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void migrateResolving() {
        System.out.println("Starting migrating Resolving...");
        if (DB_READ_OFFSET != null) {
            System.out.println("Omitting first " + DB_READ_OFFSET + " records read from DB, waiting to get there ...");
        }
        try {
            conn.setAutoCommit(false);

            String query = Resolving.query(DB_READ_LIMIT, DB_READ_OFFSET);

            try (Statement stmt = conn.createStatement()) {
                stmt.setFetchSize(DB_FETCH_SIZE == null ? 0 : DB_FETCH_SIZE);
                ResultSet resultSet = stmt.executeQuery(query);

                List<DomainIdx> batch = new ArrayList<>();
                while (resultSet.next()) {
                    String json = resultSet.getString("resulting_json");
                    try {
                        Resolving resolving = mapper.readValue(json, Resolving.class);
                        batch.add(resolving);

                    } catch (JsonProcessingException | IllegalArgumentException e) {
                        System.out.println("Failed to parse json: " + json + " --- " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }

                    if (batch.size() >= MAX_INDEXING_BATCH_SIZE) {
                        batchProcessor.accept(batch);
                        batch.clear();
                    }
                }
                // don't forget remaining to process remaining items in batch!
                if (!batch.isEmpty()) {
                    batchProcessor.accept(batch);
                    batch.clear();
                }
            }

            conn.setAutoCommit(true);
            System.out.println("Finished migrating Resolving.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}