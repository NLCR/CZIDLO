package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.RegistrarCode;
import org.joda.time.DateTime;

public class ResolvationLog {

    private Long id;
    private RegistrarCode registrarCode;
    private String documentCode;
    private DateTime resolved;

    public ResolvationLog(Long id, RegistrarCode registrarCode, String documentCode, DateTime resolved) {
        this.id = id;
        this.registrarCode = registrarCode;
        this.documentCode = documentCode;
        this.resolved = resolved;
    }

    public Long getId() {
        return id;
    }

    public RegistrarCode getRegistrarCode() {
        return registrarCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public DateTime getResolved() {
        return resolved;
    }
}
