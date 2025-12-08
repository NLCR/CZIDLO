package cz.nkp.urnnbn.indexer.es.domain.resolving;

import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;
import java.time.LocalDateTime;

public class Resolving implements DomainIdx {

    public Long id;
    public String registrarcode;
    public String documentcode;
    public LocalDateTime resolved;

    @Override
    public String getId() {
        return Long.toString(id);
    }

    public static String query(Integer limit, Integer offset) {
        return """
                SELECT
                   to_jsonb(ur.*) AS resulting_json
                FROM urnnbn_resolvation ur
                """
                + (limit != null ? "LIMIT " + limit : "")
                + (offset != null ? "OFFSET " + offset : "");

    }
}
