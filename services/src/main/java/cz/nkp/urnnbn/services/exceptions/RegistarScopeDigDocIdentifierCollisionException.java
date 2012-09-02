/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.Registrar;

/**
 *
 * @author Martin Řehánek
 */
public class RegistarScopeDigDocIdentifierCollisionException extends Exception {

    public RegistarScopeDigDocIdentifierCollisionException(Registrar registrar, DigDocIdentifier id) {
        super("digital document with registrar-scope identifier of type '" + id.getType()
                + "' and value '" + id.getValue()
                + "' already registered by registrar with code " + registrar.getCode());
    }
}
