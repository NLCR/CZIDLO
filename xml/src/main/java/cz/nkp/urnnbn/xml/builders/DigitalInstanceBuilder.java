/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceBuilder extends XmlBuilder {

    private final DigitalInstance instance;
    private final DigitalLibraryBuilder digLibBuilder;
    private final DigitalRepresentationBuilder digRepBuilder;

    public DigitalInstanceBuilder(DigitalInstance instance, DigitalLibraryBuilder digLibBuilder, DigitalRepresentationBuilder digRepBuilder) {
        this.instance = instance;
        this.digLibBuilder = digLibBuilder;
        this.digRepBuilder = digRepBuilder;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalInstance", RESOLVER);
        appendIdentifierElement(root, IDTYPE_INTERNAL, instance.getId());
        appendElementWithContentIfNotNull(root, instance.getUrl(), "url");
        appendElementWithContentIfNotNull(root, instance.getPublished(), "published");
        appendBuilderResultfNotNull(root, digLibBuilder);
        appendBuilderResultfNotNull(root, digRepBuilder);
        return root;
    }
}
