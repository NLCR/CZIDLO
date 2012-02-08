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
public class DigitalDocumentIdentifiersBuilder extends XmlBuilder {

    private final List<DigitalDocumentIdentifierBuilder> identifierBuilders;

    public DigitalDocumentIdentifiersBuilder(List<DigitalDocumentIdentifierBuilder> identifierBuilders) {
        this.identifierBuilders = identifierBuilders;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("registrarScopeIdentifiers", RESOLVER);
        for (DigitalDocumentIdentifierBuilder idBuilder : identifierBuilders) {
            appendBuilderResultfNotNull(root, idBuilder);
        }
        return root;
    }
}
