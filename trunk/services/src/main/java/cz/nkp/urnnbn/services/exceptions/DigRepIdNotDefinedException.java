/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.DigDocIdType;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdNotDefinedException extends Exception {

    public DigRepIdNotDefinedException(DigDocIdType type) {
        super("identifier for type '" + type.toString() + "' not defined");
    }
}
