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

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import nu.xom.Attribute;
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
        Element root = new Element("digitalLibrary", RESOLVER_NS);
        root.addAttribute(new Attribute("id", lib.getId().toString()));
        appendElementWithContentIfNotNull(root, lib.getName(), "name");
        appendElementWithContentIfNotNull(root, lib.getDescription(), "description");
        appendElementWithContentIfNotNull(root, lib.getUrl(), "url");
        appendTimestamps(root, lib, "digital library");
        appendBuilderResultfNotNull(root, registrarBuilder);
        return root;
    }
}
