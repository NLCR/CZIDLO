/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;

/**
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierNotDefinedException extends Exception {

    public RegistrarScopeIdentifierNotDefinedException(long digDocId, RegistrarScopeIdType type) {
        super(String.format("identifier of type '%s' not defined for digital document with id %d", type.toString(), digDocId));
    }

}
