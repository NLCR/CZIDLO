package cz.nkp.urnnbn.indexer.es;

import java.time.LocalDateTime;

public class Assigning implements ConversionType{

    public Long digitaldocumentid;
    public String documentcode;
    public String registrarcode;
    public Long registrarid;
    public String registrarname;
    public Long archiverid;
    public String archivername;
    public String entitytype;
    public LocalDateTime registered;
    public LocalDateTime reserved;
    public LocalDateTime deactivated;
    public String deactivationnote;
    public Boolean active;

    @Override
    public String toString() {
        return "Assigning{" +
                "digitaldocumentid=" + digitaldocumentid +
                ", documentcode='" + documentcode + '\'' +
                ", registrarcode='" + registrarcode + '\'' +
                ", registrarid=" + registrarid +
                ", registrarname='" + registrarname + '\'' +
                ", archiverid=" + archiverid +
                ", archivername='" + archivername + '\'' +
                ", entitytype='" + entitytype + '\'' +
                ", registered=" + registered +
                ", reserved=" + reserved +
                ", deactivated=" + deactivated +
                ", deactivationnote='" + deactivationnote + '\'' +
                ", active=" + active +
                '}';
    }


    public String query(){
        return  """
                SELECT
                	to_jsonb(u.*) ||
                    jsonb_build_object(
                		'archiverid', (
                			SELECT dd.archiverid
                		),
                
                		'archivername', (
                			SELECT archiver.name
                		),
                
                		'registrarname', (
                			SELECT ar.name
                			FROM archiver ar
                			WHERE ar.id = dd.registrarid
                		),
                
                		'registrarid', (
                			SELECT dd.registrarid
                		),
                
                		'entitytype', (
                			SELECT intelectualentity.entitytype
                		)
                   ) AS resulting_json
                   FROM urnnbn u\s
                   INNER JOIN digitaldocument dd ON u.digitaldocumentid = dd.id
                   LEFT JOIN archiver ON archiver.id = dd.archiverid
                   LEFT JOIN intelectualentity ON intelectualentity.id = dd.intelectualentityid
                """;
    }
}
