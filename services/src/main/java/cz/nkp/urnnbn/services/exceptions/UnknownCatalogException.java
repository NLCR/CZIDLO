/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownCatalogException extends Exception {

    public UnknownCatalogException(long catalogId) {
        super("unknown catalog with id: " + catalogId);
    }
}
