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

import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierBuilder extends XmlBuilder {

    private final RegistrarScopeIdentifier id;
    private final RegistrarScopeIdValue previousValue;

    public RegistrarScopeIdentifierBuilder(RegistrarScopeIdentifier id) {
        this.id = id;
        this.previousValue = null;
    }

    public RegistrarScopeIdentifierBuilder(RegistrarScopeIdentifier id, RegistrarScopeIdValue previousValue) {
        this.id = id;
        this.previousValue = previousValue;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("id", CZIDLO_NS);
        root.addAttribute(new Attribute("type", id.getType().toString()));
        if (previousValue != null) {
            root.addAttribute(new Attribute("previousValue", previousValue.toString()));
        }
        root.appendChild(id.getValue().toString());
        return root;
    }
}
