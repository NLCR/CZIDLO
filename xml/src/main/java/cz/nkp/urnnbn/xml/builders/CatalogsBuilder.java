/*
 * Copyright (C) 2011, 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        Element root = new Element("catalogs", CZIDLO_NS);
        for (Catalog catalog : catalogs) {
            CatalogBuilder builder = new CatalogBuilder(catalog, null);
            appendBuilderResultfNotNull(root, builder);
        }
        return root;
    }
}
