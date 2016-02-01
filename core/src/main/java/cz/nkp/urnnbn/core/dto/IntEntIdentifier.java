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

    private Long intEntDbId;
    private IntEntIdType type;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getIntEntDbId() {
        return intEntDbId;
    }

    public void setIntEntDbId(Long intEntDbId) {
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
        if (this.intEntDbId != other.intEntDbId && (this.intEntDbId == null || !this.intEntDbId.equals(other.intEntDbId))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.intEntDbId != null ? this.intEntDbId.hashCode() : 0);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "IntEntIdentifier{" + "intEntDbId=" + intEntDbId + ", type=" + type + ", value=" + value + '}';
    }
}
