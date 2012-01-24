/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 *
 * @author Martin Řehánek
 */
public class UrnUsedException extends Exception {

    private final UrnNbn urn;

    public UrnUsedException(UrnNbn urn) {
        this.urn = urn;
    }

    public UrnNbn getUrn() {
        return urn;
    }
}
