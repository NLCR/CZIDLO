/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.Registrar;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdentifierCollisionException extends Exception {

    public DigRepIdentifierCollisionException(Registrar registrar, DigRepIdentifier id) {
        super("digital represenation with identifier of type '" + id.getType()
                + "' and value '" + id.getValue()
                + "' already registered by registrar with sigla " + registrar.getUrnInstitutionCode());
    }
}
