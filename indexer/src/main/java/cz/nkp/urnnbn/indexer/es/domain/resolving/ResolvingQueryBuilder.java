package cz.nkp.urnnbn.indexer.es.domain.resolving;

public class ResolvingQueryBuilder {

    private static final String SELECT_CLAUSE = """
            SELECT
                to_jsonb(ur.*) ||
                jsonb_build_object(
                   'urnnbn', urnnbns.json_list,
                   'digitalborn', ie.digitalborn
               )
            """;

    private static final String FROM_CLAUSE = """
            FROM urnnbn_resolvation ur
         
            -- LATERAL for Urnnbn
            LEFT JOIN LATERAL (
                 SELECT jsonb_agg(to_jsonb(u.*)) AS json_list
                 FROM urnnbn u
                 WHERE u.documentcode = ur.documentcode AND u.registrarcode = ur.registrarcode
            ) urnnbns ON TRUE
            
            -- Standard Joins
            LEFT JOIN urnnbn u2 ON  u2.documentcode = ur.documentcode AND u2.registrarcode = ur.registrarcode
            
            LEFT JOIN digitaldocument dd ON dd.id = u2.digitaldocumentid
            
            LEFT JOIN intelectualentity ie ON ie.id = dd.intelectualentityid
            """;

    private String outputAlias = "resulting_json";
    private String whereCondition = "";
    private String limitClause = "";
    private String offsetClause = "";

    public ResolvingQueryBuilder withAlias(String alias) {
        this.outputAlias = alias;
        return this;
    }

    public ResolvingQueryBuilder where(String condition) {
        this.whereCondition = "WHERE " + condition;
        return this;
    }

    public ResolvingQueryBuilder limit(Integer limit) {
        this.limitClause = limit == null ? "" : "LIMIT " + limit;
        return this;
    }

    public ResolvingQueryBuilder offset(Integer dbReadOffset) {
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
