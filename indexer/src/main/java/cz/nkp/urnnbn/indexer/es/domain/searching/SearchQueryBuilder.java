package cz.nkp.urnnbn.indexer.es.domain.searching;

public class SearchQueryBuilder {

    private static final String SELECT_CLAUSE = """
            SELECT
                COALESCE(to_jsonb(dd.*), '{}'::jsonb) ||
                jsonb_build_object(
                    'urnnbn', urnnbns.json_list,
                    'entitytype', ie.entitytype,
                    'otheroriginator', ie.otheroriginator,
            
                    -- Pulling the results from the LATERAL aliases below
                    'ieidentifiers', ids.json_map,
                    'rsidentifiers', rsids.json_map,
                    'sourcedocument', docs.json_list,
                    'originator', orig.json_list,
                    'publication', pub.json_list
                )
            """;

    private static final String FROM_CLAUSE = """
            FROM digitaldocument dd
            
            -- Standard Joins
            LEFT JOIN intelectualentity ie ON ie.id = dd.intelectualentityid
            
            -- LATERAL for Urnnbn
            LEFT JOIN LATERAL (
                SELECT jsonb_agg(jsonb_build_object(
            		'registrarcode', u.registrarcode,\s
            		'documentcode', u.documentcode,
            		'active', u.active
            	)) AS json_list
                FROM urnnbn u
                WHERE u.digitaldocumentid = dd.id
            ) urnnbns ON TRUE
            
            -- LATERAL for Identifiers (Grouped by Type)
            LEFT JOIN LATERAL (
                SELECT jsonb_object_agg(sub.type, sub.values) AS json_map
                FROM (
                    SELECT i.type, jsonb_agg(i.idvalue) AS values
                    FROM ieidentifier i
                    WHERE i.intelectualentityid = ie.id
                    GROUP BY i.type
                ) sub
            ) ids ON TRUE
            
            -- LATERAL for Registrar-scope-identifiers (Grouped by Type)
            LEFT JOIN LATERAL (
                SELECT jsonb_object_agg(sub.type, sub.values) AS json_map
                FROM (
                    SELECT rsi.type, jsonb_agg(rsi.idvalue) AS values
                    FROM registrarscopeid rsi
                    WHERE rsi.digitaldocumentid = dd.id
                    GROUP BY rsi.type
                ) sub
            ) rsids ON TRUE
            
            -- LATERAL for Source Documents
            LEFT JOIN LATERAL (
                SELECT jsonb_agg(to_jsonb(s.*)) AS json_list
                FROM sourcedocument s
                WHERE s.intelectualentityid = ie.id
            ) docs ON TRUE
            
            -- LATERAL for Originators
            LEFT JOIN LATERAL (
                SELECT jsonb_agg(jsonb_build_object(
            		'type', o.origintype,\s
            		'value', o.originvalue
            	)) AS json_list
                FROM originator o
                WHERE o.intelectualentityid = ie.id
            ) orig ON TRUE
            
            -- LATER for Publications
            LEFT JOIN LATERAL (
                SELECT jsonb_agg(jsonb_build_object(
            		'place', p.place,\s
            		'publisher', p.publisher,
            		'pyear', p.pyear
            	)) AS json_list
                FROM publication p
                WHERE p.intelectualentityid = ie.id
            ) pub ON TRUE
            
            """;

    private String outputAlias = "resulting_json";
    private String whereCondition = "";
    private String limitClause = "";
    private String offsetClause = "";

    public SearchQueryBuilder withAlias(String alias) {
        this.outputAlias = alias;
        return this;
    }

    public SearchQueryBuilder where(String condition) {
        this.whereCondition = "WHERE " + condition;
        return this;
    }

    public SearchQueryBuilder limit(Integer limit) {
        this.limitClause = limit == null ? "" : "LIMIT " + limit;
        return this;
    }

    public SearchQueryBuilder offset(Integer dbReadOffset) {
        this.offsetClause = dbReadOffset == null ? "" : "OFFSET " + dbReadOffset;
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
