/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.List;
import nu.xom.Element;

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
    Element buildRootElement() {
        Element root = new Element("urnNbnReservation", RESOLVER);
        for (UrnNbn urnNbn : urnNbnList) {
            Element urnEl = new Element("urnNbn", RESOLVER);
            urnEl.appendChild(urnNbn.toString());
            root.appendChild(urnEl);
        }
        return root;
    }
}
