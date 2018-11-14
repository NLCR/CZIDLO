/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;

/**
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierCollisionException extends Exception {

    public RegistrarScopeIdentifierCollisionException(RegistrarCode registrarCode, RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        super(String.format("registrar-scope identifier with type '%s' and value '%s' already exists for registrar with code '%s'",
                toStringOrNull(type), toStringOrNull(value), registrarCode.toString()));
    }

    public RegistrarScopeIdentifierCollisionException(RegistrarCode registrarCode, Long digDocId, RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        super(String.format("for registrar '%s' there already exists registrar-scope identifier with either " +
                        "type '%s' for digital document %d, or " +
                        "type '%s' and value '%s' for some other digital document",
                registrarCode.toString(), toStringOrNull(type), digDocId, toStringOrNull(type), toStringOrNull(value)));
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
