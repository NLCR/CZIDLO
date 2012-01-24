/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.OriginType;

/**
 *
 * @author Martin Řehánek
 */
public class Originator implements IdentifiableByLongAttribute {

    private long intEntId;
    private OriginType type;
    private String value;

    public Originator() {
    }

    public Originator(Originator inserted) {
        intEntId = inserted.getIntEntId();
        type = inserted.getType();
        value = inserted.getValue();
    }

    public long getIntEntId() {
        return intEntId;
    }

    @Override
    public long getId() {
        return getIntEntId();
    }

    public void setIntEntId(long intEntId) {
        this.intEntId = intEntId;
    }
    
    @Override
    public void setId(long id){
        setIntEntId(intEntId);
    }

    public OriginType getType() {
        return type;
    }

    public void setType(OriginType type) {
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
        final Originator other = (Originator) obj;
        if (this.intEntId != other.intEntId) {
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
        hash = 41 * hash + (int) (this.intEntId ^ (this.intEntId >>> 32));
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
