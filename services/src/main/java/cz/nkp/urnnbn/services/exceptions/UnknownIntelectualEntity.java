/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownIntelectualEntity extends Exception {

    public UnknownIntelectualEntity(long entityId) {
        super("unknown intelectual entity " + entityId);
    }
}
