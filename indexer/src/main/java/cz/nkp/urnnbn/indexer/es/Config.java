package cz.nkp.urnnbn.indexer.es;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Config {

    public static final String INDEX_SEARCH = "czidlo_search_1"; //increment when changing logic and creating new index
    public static final String INDEX_ASSIGN = "czidlo_assign_1"; //increment when changing logic and creating new index
    public static final String INDEX_RESOLVE = "czidlo_resolve_1"; //increment when changing logic and creating new index

    public static final boolean DISABLE_INDEXING = false; //set to false in production
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

}
