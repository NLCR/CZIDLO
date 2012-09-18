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

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierBuilder extends XmlBuilder {

    private final DigDocIdentifier id;
    private final String previousValue;

    public RegistrarScopeIdentifierBuilder(DigDocIdentifier id) {
        this.id = id;
        this.previousValue = null;
    }

    public RegistrarScopeIdentifierBuilder(DigDocIdentifier id, String previousValue) {
        this.id = id;
        this.previousValue = previousValue;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("id", RESOLVER);
        Attribute type = new Attribute("type", id.getType().toString());
        root.addAttribute(type);
        root.appendChild(id.getValue());
        if (previousValue != null) {
            Element previousValueEl = appendElement(root, "previousValue");
            previousValueEl.appendChild(previousValue);
        }
        return root;
    }
}
