/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierNotDefinedException extends Exception {

    public RegistrarScopeIdentifierNotDefinedException(RegistrarScopeIdType type) {
        super("identifier for type '" + type.toString() + "' not defined");
    }
}
