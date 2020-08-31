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
package cz.nkp.urnnbn.xml.apiv6.builders;

import cz.nkp.urnnbn.core.dto.Catalog;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogBuilderXml extends XmlBuilder {

    private final Catalog catalog;
    private final RegistrarBuilder registrarBuilder;

    public CatalogBuilderXml(Catalog catalog, RegistrarBuilder registrarBuilder) {
        this.catalog = catalog;
        this.registrarBuilder = registrarBuilder;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("catalog", CZIDLO_NS);
        root.addAttribute(new Attribute("id", catalog.getId().toString()));
        appendElementWithContentIfNotNull(root, catalog.getName(), "name");
        appendElementWithContentIfNotNull(root, catalog.getDescription(), "description");
        appendElementWithContentIfNotNull(root, catalog.getUrlPrefix(), "urlPrefix");
        appendTimestamps(root, catalog, "catalog");
        appendBuilderResultfNotNull(root, registrarBuilder);
        return root;
    }
}
