/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnWithStatus {

    public static enum Status {

        FREE,
        RESERVED,
        ACTIVE,
        DEACTIVATED
    }
    private final UrnNbn urn;
    private final Status status;
    private final String note;

    public UrnNbnWithStatus(UrnNbn urn, Status status, String note) {
        this.urn = urn;
        this.status = status;
        this.note = note;
    }

    public Status getStatus() {
        return status;
    }

    public UrnNbn getUrn() {
        return urn;
    }

    public String getNote() {
        return note;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.urn != null ? this.urn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UrnNbnWithStatus other = (UrnNbnWithStatus) obj;
        if (this.urn != other.urn && (this.urn == null || !this.urn.equals(other.urn))) {
            return false;
        }
        return true;
    }
}
