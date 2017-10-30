/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import nu.xom.Document;

/**
 *
 * @author Martin Řehánek
 */
public class OaiRecord {

    private final String identifier;
    private final Document document;

    public OaiRecord(String identifier, Document document) {
        this.identifier = identifier;
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public String getIdentifier() {
        return identifier;
    }
}
