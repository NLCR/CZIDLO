/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class AlreadyPresentException extends PersistenceException {

    private final Object presentObjectId;

    public AlreadyPresentException(Object presentObjectId) {
        this.presentObjectId = presentObjectId;
    }

    public Object getPresentObjectId() {
        return presentObjectId;
    }
}
