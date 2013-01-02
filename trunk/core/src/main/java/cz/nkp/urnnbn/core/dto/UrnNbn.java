/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbn {

    private static final String PREFIX = "urn:nbn:";
    private static Pattern URN_NBN_PATTERN = null;
    private final Long digDocId;
    private final RegistrarCode registrarCode;
    private final String documentCode;
    private final DateTime created;
    private final DateTime modified;
    private final Boolean active;
    List<UrnNbnWithStatus> predecessors;
    List<UrnNbnWithStatus> successors;

    /**
     * This constructor should be used when urn:nbn is being assigned or parsed
     *
     * @param registrarCode
     * @param documentCode
     * @param digDocId
     */
    public UrnNbn(RegistrarCode registrarCode, String documentCode, Long digDocId) {
        this.registrarCode = registrarCode;
        this.documentCode = documentCode.toLowerCase();
        this.digDocId = digDocId;
        this.created = null;
        this.modified = null;
        this.active = null;
    }

    private static Pattern getUrnNbnPattern() {
        if (URN_NBN_PATTERN == null) {
            URN_NBN_PATTERN = Pattern.compile(PREFIX + CountryCode.getCode() + ":[a-zA-z0-9]{2,6}\\-[a-zA-Z0-9]{6}", Pattern.CASE_INSENSITIVE);
        }
        return URN_NBN_PATTERN;
    }

    /**
     * This constructor should be used when data is loaded from database in
     * order to be presented
     *
     * @param registrarCode
     * @param documentCode
     * @param digDocId
     * @param created
     * @param modified
     */
    public UrnNbn(RegistrarCode registrarCode, String documentCode, Long digDocId, DateTime created, DateTime modified, boolean active) {
        this.registrarCode = registrarCode;
        this.documentCode = documentCode.toLowerCase();
        this.digDocId = digDocId;
        this.created = created;
        this.modified = modified;
        this.active = active;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public Long getDigDocId() {
        return digDocId;
    }

    public RegistrarCode getRegistrarCode() {
        return registrarCode;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getModified() {
        return modified;
    }

    public Boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return (PREFIX + CountryCode.getCode() + ':' + registrarCode + "-" + documentCode).toLowerCase();
    }

    public static UrnNbn valueOf(String string) {
        //urn:nbn:cz:aba001-123456
        Matcher matcher = getUrnNbnPattern().matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("'" + string + "' doesn't match " + getUrnNbnPattern());
        }
        String[] tokens = string.split(":");
         String[] registrarAndDocumentCodes = tokens[3].split("-");
        RegistrarCode registrarCode = RegistrarCode.valueOf(registrarAndDocumentCodes[0]);
        String documentCode = registrarAndDocumentCodes[1].toLowerCase();
        return new UrnNbn(registrarCode, documentCode, null);
    }

    public List<UrnNbnWithStatus> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<UrnNbnWithStatus> predecessors) {
        this.predecessors = predecessors;
    }

    public List<UrnNbnWithStatus> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<UrnNbnWithStatus> successors) {
        this.successors = successors;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UrnNbn other = (UrnNbn) obj;
        if (this.digDocId != other.digDocId && (this.digDocId == null || !this.digDocId.equals(other.digDocId))) {
            return false;
        }
        if ((this.registrarCode == null) ? (other.registrarCode != null) : !this.registrarCode.equals(other.registrarCode)) {
            return false;
        }
        if ((this.documentCode == null) ? (other.documentCode != null) : !this.documentCode.equals(other.documentCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.digDocId != null ? this.digDocId.hashCode() : 0);
        hash = 19 * hash + (this.registrarCode != null ? this.registrarCode.hashCode() : 0);
        hash = 19 * hash + (this.documentCode != null ? this.documentCode.hashCode() : 0);
        return hash;
    }
}
