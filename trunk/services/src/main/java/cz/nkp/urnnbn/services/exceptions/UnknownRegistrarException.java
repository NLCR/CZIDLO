/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.Sigla;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownRegistrarException extends Exception {

    public UnknownRegistrarException(Sigla sigla) {
        super("unknown registrar with sigla: " + sigla);
    }

    public UnknownRegistrarException(long registrarId) {
        super("unknown registrar with id: " + registrarId);
    }
}
