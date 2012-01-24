/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class ErrorResponseBuilder extends XmlBuilder {

    private final String errorCode;
    private final String errorMessage;

    public ErrorResponseBuilder(String errorCode, String errorMessage) {
        if (errorCode == null) {
            throw new NullPointerException("errorCode");
        }
        if (errorMessage == null) {
            throw new NullPointerException("errorMessage");
        }
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("error", RESOLVER);
        appendElementWithContentIfNotNull(root, errorCode, "code");
        appendElementWithContentIfNotNull(root, errorMessage, "message");
        return root;
    }
}
