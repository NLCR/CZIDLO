/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.DigRepIdType;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdentifier {

    private long digRepId;
    private long registrarId;
    private DigRepIdType type;
    private String value;

    public DigRepIdentifier() {
    }

    public DigRepIdentifier(DigRepIdentifier inserted) {
        registrarId = inserted.getRegistrarId();
        digRepId = inserted.getDigRepId();
        type = inserted.getType();
        value = inserted.getValue();
    }

    public long getDigRepId() {
        return digRepId;
    }

    public void setDigRepId(long digRepId) {
        this.digRepId = digRepId;
    }

    public long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(long registrarId) {
        this.registrarId = registrarId;
    }

    public DigRepIdType getType() {
        return type;
    }

    public void setType(DigRepIdType type) {
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
        final DigRepIdentifier other = (DigRepIdentifier) obj;
        if (this.digRepId != other.digRepId) {
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
        hash = 79 * hash + (int) (this.digRepId ^ (this.digRepId >>> 32));
        hash = 79 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
