/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationIdentifierBuilder extends XmlBuilder {

    private final DigRepIdentifier id;

    public DigitalRepresentationIdentifierBuilder(DigRepIdentifier id) {
        this.id = id;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("id", RESOLVER);
        Attribute type = new Attribute("type", id.getType().toString());
        root.addAttribute(type);
        root.appendChild(id.getValue());
        return root;
    }
}
