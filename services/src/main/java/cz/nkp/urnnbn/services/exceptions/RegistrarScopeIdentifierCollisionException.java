/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierCollisionException extends Exception {

    public RegistrarScopeIdentifierCollisionException(RegistrarScopeIdentifier rsId, RegistrarCode registrarCode) {
        super(String.format("registrar-scope identifier with type %s and value %s  already present for registrar with code %s",
                toStringOrNull(rsId.getType()), toStringOrNull(rsId.getValue()), registrarCode.toString()));
    }

    public RegistrarScopeIdentifierCollisionException(RegistrarScopeIdentifier rsId) {
        super(String.format("registrar-scope identifier with type %s and value %s  already present for registrar with id %d",
                toStringOrNull(rsId.getType()), toStringOrNull(rsId.getValue()), rsId.getRegistrarId()));
    }

    private static String toStringOrNull(RegistrarScopeIdValue value) {
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    private static String toStringOrNull(RegistrarScopeIdType type) {
        if (type != null) {
            return type.toString();
        }
        return null;
    }

}
