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
package cz.nkp.urnnbn.xml.apiv5.builders;

import nu.xom.Element;

import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarsBuilder extends XmlBuilder {

    private final List<RegistrarBuilder> registrarBuilders;

    public RegistrarsBuilder(List<RegistrarBuilder> registrarBuilders) {
        this.registrarBuilders = registrarBuilders;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("registrars", CZIDLO_NS);
        for (RegistrarBuilder builder : registrarBuilders) {
            root.appendChild(builder.buildRootElement());
        }
        return root;
    }
}
