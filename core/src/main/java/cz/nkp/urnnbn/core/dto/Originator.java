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

    private Long intEntId;
    private OriginType type;
    private String value;

    public Originator() {
    }

    public Originator(Originator inserted) {
        intEntId = inserted.getIntEntId();
        type = inserted.getType();
        value = inserted.getValue();
    }

    public Long getIntEntId() {
        return intEntId;
    }

    public void setIntEntId(Long intEntId) {
        this.intEntId = intEntId;
    }

    @Override
    public Long getId() {
        return getIntEntId();
    }

    @Override
    public void setId(Long id) {
        setIntEntId(id);
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
        if (this.intEntId != other.intEntId && (this.intEntId == null || !this.intEntId.equals(other.intEntId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.intEntId != null ? this.intEntId.hashCode() : 0);
        return hash;
    }
}
