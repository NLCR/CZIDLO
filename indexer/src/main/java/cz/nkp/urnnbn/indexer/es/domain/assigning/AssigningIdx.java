package cz.nkp.urnnbn.indexer.es.domain.assigning;

import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;

import java.time.LocalDateTime;

public class AssigningIdx implements DomainIdx {
    //assigning
    public String entitytype;
    public String digitalborn;
    public Long digitaldocumentid;

    //urnnbn
    public String documentcode;
    public String registrarcode;
    public Boolean active;
    public LocalDateTime registered;
    public LocalDateTime reserved;
    public LocalDateTime deactivated;
    public String deactivationnote;

    //registrar
    public Long registrarid;

    //archiver
    public Long archiverid;
    public String archivername;
    public String registrarname;

    public static AssigningIdx fromDb(Assigning assigning) {
        AssigningIdx idx = new AssigningIdx();

        idx.digitaldocumentid = assigning.id;
        idx.entitytype = assigning.entitytype;
        idx.digitalborn = assigning.digitalborn;

        Assigning.UrnNbn urnNbn = assigning.urnnbn.getFirst();
        idx.documentcode = urnNbn == null ? null : urnNbn.documentcode;
        idx.registered = urnNbn == null ? null : urnNbn.registered;
        idx.reserved = urnNbn == null ? null : urnNbn.reserved;
        idx.deactivated = urnNbn == null ? null : urnNbn.deactivated;
        idx.deactivationnote = urnNbn == null ? null : urnNbn.deactivationnote;
        idx.active = urnNbn == null ? null : urnNbn.active;

        Assigning.Registrar registrar = assigning.registrar;
        idx.registrarid = registrar == null ? null : registrar.id;
        idx.registrarcode = urnNbn == null ? null : urnNbn.registrarcode;
        idx.registrarname = registrar == null ? null : registrar.name;

        Assigning.Archiver archiver = assigning.archiver;
        idx.archiverid = archiver == null ? null : archiver.id;
        idx.archivername = archiver == null ? null : archiver.name;

        return idx;
    }

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
                ", digitalborn=" + digitalborn +
                ", registered=" + registered +
                ", reserved=" + reserved +
                ", deactivated=" + deactivated +
                ", deactivationnote='" + deactivationnote + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public String getId() {
        return Long.toString(digitaldocumentid);
    }
}
