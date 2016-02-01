package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;
import java.util.List;

public class UrnNbnDTO implements Serializable {

    private static final long serialVersionUID = -8544388940624679984L;
    private static final String PREFIX = "urn:nbn:";
    private String countryCode;
    private String registrarCode;
    private String documentCode;
    private Long digdocId;
    private boolean active;
    private String reserved;
    private String registered;
    private String deactivated;
    private String deactivationNote;
    private List<UrnNbnDTO> predecessors;
    private List<UrnNbnDTO> successors;
    private String note;

    public UrnNbnDTO() {
    }

    public UrnNbnDTO(String countryCode, String registrarCode, String documentCode, Long digdocId, boolean active, String reserved,
            String registered, String deactivated, List<UrnNbnDTO> predecessors, List<UrnNbnDTO> successors, String note, String deactivationNote) {
        this.countryCode = countryCode;
        this.registrarCode = registrarCode;
        this.documentCode = documentCode;
        this.digdocId = digdocId;
        this.active = active;
        this.reserved = reserved;
        this.registered = registered;
        this.deactivated = deactivated;
        this.predecessors = predecessors;
        this.successors = successors;
        this.note = note;
        this.deactivationNote = deactivationNote;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }

    public void setRegistrarCode(String registrarCode) {
        this.registrarCode = registrarCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public Long getDigdocId() {
        return digdocId;
    }

    public void setDigdocId(Long digdocId) {
        this.digdocId = digdocId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(String deactivated) {
        this.deactivated = deactivated;
    }

    public List<UrnNbnDTO> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<UrnNbnDTO> predecessors) {
        this.predecessors = predecessors;
    }

    public List<UrnNbnDTO> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<UrnNbnDTO> successors) {
        this.successors = successors;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDeactivationNote() {
        return deactivationNote;
    }

    public void setDeactivationNote(String deactivationNote) {
        this.deactivationNote = deactivationNote;
    }

    public String toString() {
        return PREFIX + countryCode + ':' + registrarCode + '-' + documentCode;
    }
}
