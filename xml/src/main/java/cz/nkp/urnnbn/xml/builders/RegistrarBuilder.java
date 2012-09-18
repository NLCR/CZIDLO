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
