/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.DigRepIdType;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdNotDefinedException extends Exception {

    public DigRepIdNotDefinedException(DigRepIdType type) {
        super("identifier for type '" + type.toString() + "' not defined");
    }
}
