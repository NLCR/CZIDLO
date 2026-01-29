package cz.nkp.urnnbn.indexer.es.domain.resolving;

import java.time.LocalDateTime;
import java.util.List;

public class Resolving{

    public Long id;
    public String registrarcode;
    public String documentcode;
    public LocalDateTime resolved;
    public Boolean digitalborn;
    public String entitytype;
    public Registrar registrar;
    public List<UrnNbn> urnnbn;

    public static class UrnNbn {
        public String registrarcode;
        public String documentcode;
        public Boolean active;

        public String getUrnnbn() {
            if (registrarcode == null || documentcode == null) {
                return null;
            }
            return "urn:nbn:cz:" + registrarcode + "-" + documentcode;
        }
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

    public static class Registrar {
        public String name;
    }
}
