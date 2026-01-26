package cz.nkp.urnnbn.indexer.es.domain.resolving;

import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;

import java.time.LocalDateTime;

public class ResolvingIdx implements DomainIdx {
    //resolving
    public Long id;
    public String registrarcode;
    public String documentcode;
    public LocalDateTime resolved;

    //urnnbn
    public Boolean active;

    //digitaldocument
    public Boolean digitalborn;

    public static ResolvingIdx fromDb(Resolving resolving) {
        ResolvingIdx idx = new ResolvingIdx();

        idx.id = resolving.id;
        idx.registrarcode = resolving.registrarcode;
        idx.documentcode = resolving.documentcode;
        idx.resolved = resolving.resolved;
        idx.digitalborn = resolving.digitalborn;

        Resolving.UrnNbn urnNbn = null;
        if (resolving.urnnbn != null && !resolving.urnnbn.isEmpty()) {
            urnNbn = resolving.urnnbn.getFirst();
        }
        idx.active = urnNbn == null ? null : urnNbn.active;

        return idx;
    }


    @Override
    public String toString() {
        return "Resolving{" +
                "id=" + id +
                ", documentcode='" + documentcode + '\'' +
                ", registrarcode='" + registrarcode + '\'' +
                ", resolved=" + resolved +
                ", active=" + active +
                ", digitalborn=" + digitalborn +
                '}';
    }

    @Override
    public String getId() {
        return Long.toString(id);
    }
}
