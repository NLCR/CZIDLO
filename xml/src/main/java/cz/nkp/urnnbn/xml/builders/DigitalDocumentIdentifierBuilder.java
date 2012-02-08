/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentIdentifierBuilder extends XmlBuilder {

    private final DigDocIdentifier id;
    private final String previousValue;

    public DigitalDocumentIdentifierBuilder(DigDocIdentifier id) {
        this.id = id;
        this.previousValue = null;
    }

    public DigitalDocumentIdentifierBuilder(DigDocIdentifier id, String previousValue) {
        this.id = id;
        this.previousValue = previousValue;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("id", RESOLVER);
        Attribute type = new Attribute("type", id.getType().toString());
        root.addAttribute(type);
        root.appendChild(id.getValue());
        if (previousValue != null) {
            Element previousValueEl = addElement(root, "previousValue");
            previousValueEl.appendChild(previousValue);
        }
        return root;
    }
}
