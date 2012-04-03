/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.Catalog;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogBuilder extends XmlBuilder {

    private final Catalog catalog;
    private final RegistrarBuilder registrarBuilder;

    public CatalogBuilder(Catalog catalog, RegistrarBuilder registrarBuilder) {
        this.catalog = catalog;
        this.registrarBuilder = registrarBuilder;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("catalog", RESOLVER);
        //appendIdentifierElement(root, IDTYPE_INTERNAL, catalog.getId());
        appendTimestamps(root, catalog, "catalog");
        appendElementWithContentIfNotNull(root, catalog.getName(), "name");
        appendElementWithContentIfNotNull(root, catalog.getDescription(), "description");
        appendElementWithContentIfNotNull(root, catalog.getUrlPrefix(), "urlPrefix");
        appendBuilderResultfNotNull(root, registrarBuilder);
        return root;
    }
}
