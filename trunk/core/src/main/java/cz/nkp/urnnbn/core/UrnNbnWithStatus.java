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
        ABANDONED
    }
    private final UrnNbn urn;
    private final Status status;

    public UrnNbnWithStatus(UrnNbn urn, Status status) {
        this.urn = urn;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public UrnNbn getUrn() {
        return urn;
    }
}
