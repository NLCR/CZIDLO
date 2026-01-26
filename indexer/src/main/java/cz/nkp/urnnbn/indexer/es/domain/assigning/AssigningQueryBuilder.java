package cz.nkp.urnnbn.indexer.es.domain.assigning;

public class AssigningQueryBuilder {

    private static final String SELECT_CLAUSE = """
            SELECT
                to_jsonb(dd.*) ||
                jsonb_build_object(
                   'urnnbn', urnnbns.json_list,
                   'entitytype', ie.entitytype,
                   'digitalborn', ie.digitalborn,
                   'archiver', archiver_json.obj,
                   'registrar', registrar_json.obj
               )
            """;

    private static final String FROM_CLAUSE = """
            FROM digitaldocument dd
            
            -- Standard Joins
            LEFT JOIN intelectualentity ie ON ie.id = dd.intelectualentityid
            LEFT JOIN archiver ar ON ar.id = dd.archiverid
            LEFT JOIN archiver reg ON reg.id = dd.registrarid
            
            -- LATERAL for Urnnbn
            LEFT JOIN LATERAL (
                 SELECT jsonb_agg(to_jsonb(u.*)) AS json_list
                 FROM urnnbn u
                 WHERE u.digitaldocumentid = dd.id
            ) urnnbns ON TRUE
            
            -- LATERAL for Archiver
            LEFT JOIN LATERAL (
                 SELECT jsonb_build_object('id', a.id, 'name', a.name) AS obj
                 FROM archiver a
                 WHERE a.id = dd.archiverid
            ) archiver_json ON TRUE
            
            -- LATERAL for Registrar
            LEFT JOIN LATERAL (
                SELECT jsonb_build_object('id', r.id, 'name', r.name) AS obj
                FROM archiver r
                WHERE r.id = dd.registrarid
            ) registrar_json ON TRUE
            """;

    private String outputAlias = "resulting_json";
    private String whereCondition = "";
    private String limitClause = "";
    private String offsetClause = "";

    public AssigningQueryBuilder withAlias(String alias) {
        this.outputAlias = alias;
        return this;
    }

    public AssigningQueryBuilder where(String condition) {
        this.whereCondition = "WHERE " + condition;
        return this;
    }

    public AssigningQueryBuilder limit(Integer limit) {
        this.limitClause = limit == null ? "" : "LIMIT " + limit;
        return this;
    }

    public AssigningQueryBuilder offset(Integer dbReadOffset) {
        this.offsetClause = dbReadOffset == null ? "" : " OFFSET " + dbReadOffset;
        return this;
    }

    public String build() {
        return SELECT_CLAUSE +
                " AS " + outputAlias + " \n" +
                FROM_CLAUSE + " \n" +
                whereCondition + " \n" +
                limitClause + " \n" +
                offsetClause + ";";
    }
}
