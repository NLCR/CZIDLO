/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

/**
 *
 * @author hanis
 */
public class SingleRecordProcessingException extends Exception {

    SingleRecordProcessingException(String msg, Throwable e) {
        super(msg, e);
    }

    SingleRecordProcessingException(String msg) {
        super(msg);
    }

}
