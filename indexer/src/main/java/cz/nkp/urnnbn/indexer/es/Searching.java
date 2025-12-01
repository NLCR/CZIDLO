package cz.nkp.urnnbn.indexer.es;

import java.util.List;

public class Searching implements ConversionType{

    public String documentcode;
    public String registrarcode;
    public String entitytype;
    public Boolean active;
    public String title;
    public String subtitle;
    public String volumetitle;
    public String issuetitle;
    public String sdtitle;
    public String sdvolumetitle;
    public String sdissuetitle;
    public String ccnb;
    public String isbn;
    public String issn;
    public String otherid;
    public List<String> author;
    public List<String> corporation;
    public List<String> event;
    public String otheroriginator;

    public String getUrnnbn() {
        if (registrarcode == null || documentcode == null) {
            return null;
        }
        return "urn:nbn:cz:" + registrarcode + "-" + documentcode;
    }

    @Override
    public String toString() {
        return "Search{" +
                "documentcode='" + documentcode + '\'' +
                ", registrarcode='" + registrarcode + '\'' +
                ", entitytype='" + entitytype + '\'' +
                ", active=" + active +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", volumetitle='" + volumetitle + '\'' +
                ", issuetitle='" + issuetitle + '\'' +
                ", sdtitle='" + sdtitle + '\'' +
                ", sdvolumetitle='" + sdvolumetitle + '\'' +
                ", sdissuetitle='" + sdissuetitle + '\'' +
                ", ccnb='" + ccnb + '\'' +
                ", isbn='" + isbn + '\'' +
                ", issn='" + issn + '\'' +
                ", otherid='" + otherid + '\'' +
                ", author=" + author +
                ", corporation=" + corporation +
                ", event=" + event +
                ", otheroriginator='" + otheroriginator + '\'' +
                '}';
    }


    public String query(){
        return """
                SELECT
                    COALESCE ( to_jsonb(u.*), '{}' :: jsonb) ||
                    jsonb_build_object(
                    
                        'registrarcode', u.registrarcode,
    
                        'documentcode', u.documentcode,
                                    
                        'entitytype', intelectualentity.entitytype,
    
                        'title', (
                            SELECT i.idvalue
                            FROM ieidentifier i
                            WHERE i.type = 'TITLE' AND intelectualentity.id = i.intelectualentityid
                        ),
    
                        'subtitle', (
                            SELECT i.idvalue
                            FROM ieidentifier i
                            WHERE i.type = 'SUB_TITLE' AND intelectualentity.id = i.intelectualentityid
                        ),
    
                        'volumetitle', (
                            SELECT i.idvalue
                            FROM ieidentifier i
                            WHERE i.type = 'VOLUME_TITLE' AND intelectualentity.id = i.intelectualentityid
                        ),
    
                        'issuetitle', (
                            SELECT i.idvalue
                            FROM ieidentifier i
                            WHERE i.type = 'ISSUE_TITLE' AND intelectualentity.id = i.intelectualentityid
                        ),
    
                        'sdtitle', sourcedocument.title,
    
                        'sdvolumetitle', sourcedocument.volumetitle,
    
                        'sdissuetitle', sourcedocument.issuetitle,
    
                        'ccnb', sourcedocument.ccnb,
    
                        'isbn', sourcedocument.isbn,
    
                        'issn', sourcedocument.issn,
    
                        'otherid', sourcedocument.otherid,
    
                        'author', (
                            SELECT json_agg(o.originvalue)
                            FROM originator o
                            WHERE o.origintype = 'AUTHOR' AND intelectualentity.id = o.intelectualentityid
                        ),
    
                        'corporation', (
                            SELECT json_agg(o.originvalue)
                            FROM originator o
                            WHERE o.origintype = 'CORPORATION' AND intelectualentity.id = o.intelectualentityid
                        ),
    
                        'event', (
                            SELECT json_agg(o.originvalue)
                            FROM originator o
                            WHERE o.origintype = 'EVENT' AND intelectualentity.id = o.intelectualentityid
                        ),
    
                        'otheroriginator', intelectualentity.otheroriginator
    
                    ) AS resulting_json
                 FROM urnnbn u\s
                 FULL OUTER JOIN digitaldocument dd ON u.digitaldocumentid = dd.id\s
                 FULL OUTER JOIN intelectualentity ON intelectualentity.id = dd.intelectualentityid
                 FULL OUTER JOIN sourcedocument ON sourcedocument.intelectualentityid = intelectualentity.id\s
                 FULL OUTER JOIN originator ON originator.intelectualentityid = intelectualentity.id
        
        """;
    }
}
