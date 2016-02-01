/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

/**
 *
 * @author hanis
 */
public class OaiAdapterException extends Exception {

    OaiAdapterException(String msg, Throwable e) {
        super(msg, e);
    }

    OaiAdapterException(String msg) {
        super(msg);
    }

}
