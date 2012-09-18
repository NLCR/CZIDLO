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

import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifiersBuilder extends XmlBuilder {

    private final List<RegistrarScopeIdentifierBuilder> identifierBuilders;

    public RegistrarScopeIdentifiersBuilder(List<RegistrarScopeIdentifierBuilder> identifierBuilders) {
        this.identifierBuilders = identifierBuilders;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("registrarScopeIdentifiers", RESOLVER);
        for (RegistrarScopeIdentifierBuilder idBuilder : identifierBuilders) {
            appendBuilderResultfNotNull(root, idBuilder);
        }
        return root;
    }
}
