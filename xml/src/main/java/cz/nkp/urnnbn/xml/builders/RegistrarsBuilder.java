/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import java.util.List;
import nu.xom.Element;

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
    Element buildRootElement() {
        Element root = new Element("registrars", RESOLVER);
        for (RegistrarBuilder builder : registrarBuilders) {
            root.appendChild(builder.buildRootElement());
        }
        return root;
    }
}
