/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceBuilder extends XmlBuilder {

    private final DigitalInstance instance;
    private final DigitalLibraryBuilder digLibBuilder;
    private final Long digLibId;
    private final DigitalDocumentBuilder digDocBuilder;

    public DigitalInstanceBuilder(DigitalInstance instance, DigitalLibraryBuilder digLibBuilder, DigitalDocumentBuilder digDocBuilder) {
        this.instance = instance;
        this.digLibBuilder = digLibBuilder;
        this.digLibId = null;
        this.digDocBuilder = digDocBuilder;
    }

    public DigitalInstanceBuilder(DigitalInstance instance, Long digLibId) {
        this.instance = instance;
        this.digLibBuilder = null;
        this.digLibId = digLibId;
        this.digDocBuilder = null;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalInstance", RESOLVER);
        //todo: dodat do databáze atribut active a při odstraňování nastavit na false
        //taky dodat datum změny a vypisovat jako "deleted" pokud nebude null (tj. odstraněno)
        root.addAttribute(new Attribute("active", "true"));
        appendIdentifierElement(root, IDTYPE_INTERNAL, instance.getId());
        appendTimestamps(root, instance, "digital instance");
        appendElementWithContentIfNotNull(root, instance.getUrl(), "url");
        appendElementWithContentIfNotNull(root, instance.getFormat(), "format");
        appendElementWithContentIfNotNull(root, instance.getAccessibility(), "accessibility");
        appendBuilderResultfNotNull(root, digLibBuilder);
        appendBuilderResultfNotNull(root, digDocBuilder);
        appendElementWithContentIfNotNull(root, digLibId, "digitalLibraryId");
        return root;
    }
}
