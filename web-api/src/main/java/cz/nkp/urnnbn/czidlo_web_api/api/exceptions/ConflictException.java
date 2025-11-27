package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

public class ConflictException extends Exception {

    /**
     * Creates a new instance of <code>ConflictException</code> without detail message.
     */
    public ConflictException() {
    }

    /**
     * Constructs an instance of <code>ConflictException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConflictException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of <code>ConflictException</code> without cause exception.
     *
     * @param cause cause
     */
    public ConflictException(Throwable cause) {
        super(cause);
    }
}
