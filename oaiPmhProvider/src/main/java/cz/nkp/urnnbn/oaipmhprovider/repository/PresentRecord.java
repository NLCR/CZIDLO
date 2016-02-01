/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import org.dom4j.Document;

/**
 *
 * @author Martin Řehánek
 */
public interface PresentRecord extends Record {

    public Document getMetadata();
}
