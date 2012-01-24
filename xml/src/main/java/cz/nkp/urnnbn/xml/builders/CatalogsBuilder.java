/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.Catalog;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogsBuilder extends XmlBuilder {

    private final List<Catalog> catalogs;

    public CatalogsBuilder(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    @Override
   Element buildRootElement() {
        Element root = new Element("catalogs", RESOLVER);
        for (Catalog catalog : catalogs) {
            CatalogBuilder builder = new CatalogBuilder(catalog, null);
            appendBuilderResultfNotNull(root, builder);
        }
        return root;
    }
}
