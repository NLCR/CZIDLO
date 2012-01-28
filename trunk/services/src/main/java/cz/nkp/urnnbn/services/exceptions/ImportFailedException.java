/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 * @deprecated 
 * @author Martin Řehánek
 */
public class ImportFailedException extends Exception {

    private Long id;

    public ImportFailedException() {
    }

    public ImportFailedException(Long id) {
        this.id = id;
    }

    public ImportFailedException(String message) {
        super(message);
    }

    public ImportFailedException(Exception cause) {
        super(cause);
    }

    public Long getId() {
        return id;
    }
}
