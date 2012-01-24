/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationIdentifiersBuilder extends XmlBuilder {

    private final List<DigitalRepresentationIdentifierBuilder> identifierBuilders;

    public DigitalRepresentationIdentifiersBuilder(List<DigitalRepresentationIdentifierBuilder> identifierBuilders) {
        this.identifierBuilders = identifierBuilders;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("registrarUniqueIds", RESOLVER);
        for (DigitalRepresentationIdentifierBuilder idBuilder : identifierBuilders) {
            appendBuilderResultfNotNull(root, idBuilder);
        }
        return root;
    }
}
