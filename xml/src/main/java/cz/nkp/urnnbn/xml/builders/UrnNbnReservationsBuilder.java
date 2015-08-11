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

import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationsBuilder extends XmlBuilder {

    private static final int MAX_RESERVATION_TO_BE_PRINTED = 50;
    private final int maxReservationSize;
    private final int defaultReservationSize;
    private final List<UrnNbn> urnNbnList;
    private final Integer reservedSize;

    public UrnNbnReservationsBuilder(int maxReservationSize, int defaultReservationSize, List<UrnNbn> urnNbnList) {
        this.maxReservationSize = maxReservationSize;
        this.defaultReservationSize = defaultReservationSize;
        this.urnNbnList = urnNbnList;
        reservedSize = urnNbnList.size();
    }

    public UrnNbnReservationsBuilder(int maxReservationSize, int defaultReservationSize, Integer soFarReserved) {
        this.maxReservationSize = maxReservationSize;
        this.defaultReservationSize = defaultReservationSize;
        this.reservedSize = soFarReserved;
        this.urnNbnList = null;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("urnNbnReservations", CZIDLO_NS);
        appendElementWithContentIfNotNull(root, maxReservationSize, "maxReservationSize");
        appendElementWithContentIfNotNull(root, defaultReservationSize, "defaultReservationSize");
        Element reservations = new Element("reserved", CZIDLO_NS);
        root.appendChild(reservations);
        Attribute size = new Attribute("totalSize", reservedSize.toString());
        reservations.addAttribute(size);

        if (urnNbnList != null && !urnNbnList.isEmpty()) {
            appendUrnNbnsFromList(reservations);
        }
        return root;
    }

    private void appendUrnNbnsFromList(Element root) {
        int counter = 0;
        for (UrnNbn urnNbn : urnNbnList) {
            if (++counter > MAX_RESERVATION_TO_BE_PRINTED) {
                break;
            }
            Element element = new Element("urnNbn", CZIDLO_NS);
            element.appendChild(urnNbn.toString());
            Attribute reserved = new Attribute("reserved", urnNbn.getReserved().toString());
            element.addAttribute(reserved);
            root.appendChild(element);
        }
    }
}
