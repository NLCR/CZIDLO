/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;

/**
 *
 * @author Martin Řehánek
 */
public class RegistarScopeIdentifierCollisionException extends Exception {

    public RegistarScopeIdentifierCollisionException(Registrar registrar, RegistrarScopeIdentifier id) {
        super("digital document with registrar-scope identifier of type '" + id.getType()
                + "' and value '" + id.getValue()
                + "' already registered by registrar with code " + registrar.getCode());
    }
}
