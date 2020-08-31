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

import cz.nkp.urnnbn.core.dto.Archiver;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverBuilderXml extends XmlBuilder {

    private final Archiver archiver;

    public ArchiverBuilderXml(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("archiver", CZIDLO_NS);
        root.addAttribute(new Attribute("id", archiver.getId().toString()));
        appendElementWithContentIfNotNull(root, archiver.getName(), "name");
        appendElementWithContentIfNotNull(root, archiver.getDescription(), "description");
        appendTimestamps(root, archiver, "archiver");
        return root;
    }
}
