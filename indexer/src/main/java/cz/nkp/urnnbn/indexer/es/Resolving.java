package cz.nkp.urnnbn.indexer.es;

import java.time.LocalDateTime;

public class Resolving implements ConversionType{

    public String registrarcode;
    public String registrarname;
    public Integer year;
    public Integer month;
    public Integer sum;


    public static class Item {
        public String registrarcode;
        public String registrarname;
        public LocalDateTime resolved;
    }


    public String query(){
        return """
                SELECT
                	to_jsonb(ur.*) ||
                    jsonb_build_object(
                
                		'registrarname', (
                			SELECT archiver.name
                		)
                
                   ) AS resulting_json
                   FROM urnnbn_resolvation_statistics ur\s
                   LEFT JOIN registrar ON registrar.code = ur.registrarcode
                   LEFT JOIN archiver ON archiver.id = registrar.id\s
                """;
    }
}
