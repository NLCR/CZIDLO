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

    private long digDocId;
    private long registrarId;
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

    public long getDigDocId() {
        return digDocId;
    }

    public void setDigDocId(long digDocId) {
        this.digDocId = digDocId;
    }

    public long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(long registrarId) {
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
        if (this.digDocId != other.digDocId) {
            return false;
        }
        if (this.registrarId != other.registrarId) {
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
        hash = 53 * hash + (int) (this.digDocId ^ (this.digDocId >>> 32));
        hash = 53 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DigDocIdentifier{" + "digDocId=" + digDocId + ", registrarId=" + registrarId + ", type=" + type + ", value=" + value + '}';
    }
}
