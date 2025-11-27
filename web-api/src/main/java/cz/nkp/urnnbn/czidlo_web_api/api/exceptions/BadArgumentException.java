package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

public class BadArgumentException extends Exception {

    /**
     * Creates a new instance of <code>BadArgumentException</code> without detail message.
     */
    public BadArgumentException() {
    }

    /**
     * Constructs an instance of <code>BadArgumentException</code> with the specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public BadArgumentException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of <code>BadArgumentException</code> without cause exception.
     *
     * @param cause
     *            cause
     */
    public BadArgumentException(Throwable cause) {
        super(cause);
    }
}
