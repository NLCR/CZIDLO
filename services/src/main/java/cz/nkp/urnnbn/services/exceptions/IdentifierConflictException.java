/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class IdentifierConflictException extends Exception {

    public IdentifierConflictException(String type, String value) {
        super("identifier of type '" + type + "' with value '" + value + "' already present");
    }
}
