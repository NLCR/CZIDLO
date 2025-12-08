package cz.nkp.urnnbn.core.dto;

import org.joda.time.DateTime;

public class ResolvationLog {

    private Long id;
    private String registrarCode;
    private String documentCode;
    private DateTime resolved;

    public ResolvationLog(Long id, String registrarCode, String documentCode, DateTime resolved) {
        this.id = id;
        this.registrarCode = registrarCode;
        this.documentCode = documentCode;
        this.resolved = resolved;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public DateTime getResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return "ResolvationLog{" +
                "id=" + id +
                ", registrarCode='" + registrarCode + '\'' +
                ", documentCode='" + documentCode + '\'' +
                ", resolved=" + resolved +
                '}';
    }
}
