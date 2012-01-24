/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class DatabaseException extends Exception {

//    /**
//     * Creates a new instance of <code>DatabaseException</code> without detail message.
//     */
//    public DatabaseException() {
//    }
//
//
//    /**
//     * Constructs an instance of <code>DatabaseException</code> with the specified detail message.
//     * @param msg the detail message.
//     */
//    public DatabaseException(String msg) {
//        super(msg);
//    }
    public DatabaseException(Exception ex) {
        super(ex);
    }

//    public DatabaseException(String message, Exception ex) {
//        super(message, ex);
//    }
}
