/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        Element root = new Element("urnNbnRerservations", RESOLVER);
        appendElementWithContentIfNotNull(root, maxReservationSize, "maxReservationSize");
        appendElementWithContentIfNotNull(root, defaultReservationSize, "defaultReservationSize");
        Element reserved = new Element("reserved", RESOLVER);
        root.appendChild(reserved);
        Attribute size = new Attribute("size", reservedSize.toString());
        reserved.addAttribute(size);
        if (urnNbnList != null) {
            appendUrnNbnsFromList(reserved);
        }
        return root;
    }

    private void appendUrnNbnsFromList(Element root) {
        for (UrnNbn urnNbn : urnNbnList) {
            Element element = new Element("urnNbn", RESOLVER);
            element.appendChild(urnNbn.toString());
            Attribute created = new Attribute("created", urnNbn.getCreated().toString());
            element.addAttribute(created);
            root.appendChild(element);
        }
    }
}
