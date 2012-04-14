/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.DigDocIdType;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigDocIdentifier {

    private Long digDocId;
    private Long registrarId;
    private DateTime created;
    private DateTime modified;
    private DigDocIdType type;
    private String value;

    public DigDocIdentifier() {
    }

    public DigDocIdentifier(DigDocIdentifier inserted) {
        registrarId = inserted.getRegistrarId();
        digDocId = inserted.getDigDocId();
        type = inserted.getType();
        value = inserted.getValue();
    }

    public Long getDigDocId() {
        return digDocId;
    }

    public void setDigDocId(Long digDocId) {
        this.digDocId = digDocId;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Long registrarId) {
        this.registrarId = registrarId;
    }

    public DigDocIdType getType() {
        return type;
    }

    public void setType(DigDocIdType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DigDocIdentifier other = (DigDocIdentifier) obj;
        if (this.digDocId != other.digDocId && (this.digDocId == null || !this.digDocId.equals(other.digDocId))) {
            return false;
        }
        if (this.registrarId != other.registrarId && (this.registrarId == null || !this.registrarId.equals(other.registrarId))) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.digDocId != null ? this.digDocId.hashCode() : 0);
        hash = 79 * hash + (this.registrarId != null ? this.registrarId.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
