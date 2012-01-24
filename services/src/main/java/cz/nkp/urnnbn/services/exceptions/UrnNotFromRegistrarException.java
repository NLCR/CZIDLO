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
public class UrnNotFromRegistrarException extends Exception {

    private final String registrarCode;
    private final UrnNbn urn;

    public UrnNotFromRegistrarException(String registrarCode, UrnNbn urn) {
        this.registrarCode = registrarCode;
        this.urn = urn;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }

    public UrnNbn getUrn() {
        return urn;
    }
}
