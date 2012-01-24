/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.IntEntIdType;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntIdentifier {

    private long intEntDbId;
    private IntEntIdType type;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getIntEntDbId() {
        return intEntDbId;
    }

    public void setIntEntDbId(long intEntDbId) {
        this.intEntDbId = intEntDbId;
    }

    public IntEntIdType getType() {
        return type;
    }

    public void setType(IntEntIdType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntEntIdentifier other = (IntEntIdentifier) obj;
        if (this.intEntDbId != other.intEntDbId) {
            return false;
        }
        if (this.type != other.type) {
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
        hash = 17 * hash + (int) (this.intEntDbId ^ (this.intEntDbId >>> 32));
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
