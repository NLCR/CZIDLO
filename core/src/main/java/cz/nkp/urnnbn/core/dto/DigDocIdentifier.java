/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.DigDocIdType;

/**
 *
 * @author Martin Řehánek
 */
public class DigDocIdentifier {

    private long digDocId;
    private long registrarId;
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
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.digDocId ^ (this.digDocId >>> 32));
        hash = 79 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DigDocIdentifier{" + "digDocId=" + digDocId + ", registrarId=" + registrarId + ", type=" + type + ", value=" + value + '}';
    }
}
