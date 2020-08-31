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

import nu.xom.Attribute;
import nu.xom.Element;

import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstancesBuilderXml extends XmlBuilder {

    private final long count;
    private final List<DigitalInstanceBuilderXml> instanceBuilderList;

    public DigitalInstancesBuilderXml(long count) {
        this.count = count;
        this.instanceBuilderList = null;
    }

    public DigitalInstancesBuilderXml(List<DigitalInstanceBuilderXml> instancesBuilders) {
        this.count = instancesBuilders.size();
        this.instanceBuilderList = instancesBuilders;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("digitalInstances", CZIDLO_NS);
        root.addAttribute(new Attribute("count", Long.toString(count)));
        if (instanceBuilderList != null) {
            for (DigitalInstanceBuilderXml builder : instanceBuilderList) {
                appendBuilderResultfNotNull(root, builder);
            }
        }
        return root;
    }
}
