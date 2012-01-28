/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownArchiverException extends Exception {

    public UnknownArchiverException(Long archiverId) {
        super("unknown archiver with id '" + archiverId + "'");
    }
}
