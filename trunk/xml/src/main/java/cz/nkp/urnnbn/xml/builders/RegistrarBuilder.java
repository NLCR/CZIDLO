/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.Registrar;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarBuilder extends XmlBuilder {

    private final Registrar registrar;
    private final DigitalLibrariesBuilder librariesBuilder;
    private final CatalogsBuilder catalogsBuilder;

    public RegistrarBuilder(Registrar registrar, DigitalLibrariesBuilder libsBuilder, CatalogsBuilder catsBuilder) {
        this.registrar = registrar;
        this.librariesBuilder = libsBuilder;
        this.catalogsBuilder = catsBuilder;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("registrar", RESOLVER);
        root.addAttribute(new Attribute("code", registrar.getCode().toString()));
        appendTimestamps(root, registrar, "registrar");
        appendElementWithContentIfNotNull(root, registrar.getName(), "name");
        appendElementWithContentIfNotNull(root, registrar.getDescription(), "description");
        appendBuilderResultfNotNull(root, librariesBuilder);
        appendBuilderResultfNotNull(root, catalogsBuilder);
        return root;
    }
}
