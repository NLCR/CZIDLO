/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibraryBuilder extends XmlBuilder {

    private final DigitalLibrary lib;
    private final RegistrarBuilder registrarBuilder;

    public DigitalLibraryBuilder(DigitalLibrary lib, RegistrarBuilder registrarBuilder) {
        this.lib = lib;
        this.registrarBuilder = registrarBuilder;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalLibrary", RESOLVER);
        appendIdentifierElement(root, IDTYPE_INTERNAL, lib.getId());
        appendElementWithContentIfNotNull(root, lib.getName(), "name");
        appendElementWithContentIfNotNull(root, lib.getDescription(), "description");
        appendBuilderResultfNotNull(root, registrarBuilder);
        return root;
    }
}
