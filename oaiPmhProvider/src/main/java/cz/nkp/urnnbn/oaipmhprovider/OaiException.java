/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.nkp.urnnbn.oaipmhprovider;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class OaiException extends Exception {

    private final ErrorCode code;

    /**
     * Constructs an instance of <code>OaiException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public OaiException(ErrorCode code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * @return the code
     */
    public ErrorCode getCode() {
        return code;
    }
}
