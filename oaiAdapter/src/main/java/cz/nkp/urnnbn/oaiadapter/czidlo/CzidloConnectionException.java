/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.czidlo;

/**
 *
 * @author hanis
 */
public class CzidloConnectionException extends Exception {

    CzidloConnectionException() {
    }

    CzidloConnectionException(String msg) {
        super(msg);
    }

    CzidloConnectionException(Throwable cause) {
        super(cause);
    }
}
