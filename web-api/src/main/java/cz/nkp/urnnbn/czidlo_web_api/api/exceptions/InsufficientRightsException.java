package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

public class InsufficientRightsException extends Exception {

    /**
     * Creates a new instance of <code>InsufficientRightsException</code> without detail message.
     */
    public InsufficientRightsException() {
    }

    /**
     * Constructs an instance of <code>InsufficientRightsException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InsufficientRightsException(String msg) {
        super(msg);
    }
}
