package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

public class DuplicateRecordException extends Exception {

    /**
     * Creates a new instance of <code>DuplicateRecordException</code> without detail message.
     */
    public DuplicateRecordException() {
    }

    /**
     * Constructs an instance of <code>DuplicateRecordException</code> with the specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public DuplicateRecordException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of <code>DuplicateRecordException</code> without cause exception.
     *
     * @param cause
     *            cause
     */
    public DuplicateRecordException(Throwable cause) {
        super(cause);
    }
}
