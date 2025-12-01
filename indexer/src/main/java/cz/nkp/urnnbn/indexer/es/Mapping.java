package cz.nkp.urnnbn.indexer.es;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Mapping {

    /**
     * Používá se na serializaci/deserializaci mezi databází a ES
     * Čili co vytáhneme z DB, to pošleme do ES a naopak, žádná volnost dalších transformací
     * Jen konfigurace formatování datumu a ignorování neznámých properties při deserializaci
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
