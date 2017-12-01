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

import cz.nkp.urnnbn.core.dto.UrnNbn;
import nu.xom.Element;

import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationBuilder extends XmlBuilder {

    private final List<UrnNbn> urnNbnList;

    public UrnNbnReservationBuilder(List<UrnNbn> urnNbnList) {
        this.urnNbnList = urnNbnList;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("urnNbnReservation", CZIDLO_NS);
        for (UrnNbn urnNbn : urnNbnList) {
            Element urnEl = new Element("urnNbn", CZIDLO_NS);
            urnEl.appendChild(urnNbn.toString());
            root.appendChild(urnEl);
        }
        return root;
    }
}
