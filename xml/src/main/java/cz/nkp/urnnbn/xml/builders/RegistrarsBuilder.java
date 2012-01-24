/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.Registrar;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarsBuilder extends XmlBuilder {

    private final List<Registrar> registrars;

    public RegistrarsBuilder(List<Registrar> registrars) {
        this.registrars = registrars;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("registrars", RESOLVER);
        for (Registrar registrar : registrars) {
            RegistrarBuilder builder = new RegistrarBuilder(registrar, null, null);
            root.appendChild(builder.buildRootElement());
        }
        return root;
    }
}
