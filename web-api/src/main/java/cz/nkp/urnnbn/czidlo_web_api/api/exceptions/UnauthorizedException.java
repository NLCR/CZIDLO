package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

public class UnauthorizedException extends Exception {

    /**
     * Creates a new instance of <code>UnauthorizedException</code> without detail message.
     */
    public UnauthorizedException() {
    }

    /**
     * Constructs an instance of <code>UnauthorizedException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
