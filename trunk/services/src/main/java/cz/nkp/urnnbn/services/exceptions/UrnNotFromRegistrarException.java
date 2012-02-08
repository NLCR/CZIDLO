/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNotFromRegistrarException extends Exception {

    private final RegistrarCode registrarCode;
    private final UrnNbn urn;

    public UrnNotFromRegistrarException(RegistrarCode registrarCode, UrnNbn urn) {
        super("registrar code should be '" + registrarCode + "'");
        this.registrarCode = registrarCode;
        this.urn = urn;
    }

    public RegistrarCode getRegistrarCode() {
        return registrarCode;
    }

    public UrnNbn getUrn() {
        return urn;
    }
}
