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
public class SyntaxException extends RuntimeException {

    /**
     * Creates a new instance of <code>SyntaxException</code> without detail message.
     */
    public SyntaxException() {
    }


    /**
     * Constructs an instance of <code>SyntaxException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(SQLException e) {
        super(e);
    }
}
