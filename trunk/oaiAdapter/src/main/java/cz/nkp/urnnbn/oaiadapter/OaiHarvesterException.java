/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

/**
 *
 * @author hanis
 */
public class OaiHarvesterException extends Exception {
    
    private String url;
    
    OaiHarvesterException() {
    }

    OaiHarvesterException(String msg, String url) {
        super(msg);
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
}