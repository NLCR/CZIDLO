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
package cz.nkp.urnnbn.xml.apiv3.builders;

import nu.xom.Attribute;
import nu.xom.Element;
import cz.nkp.urnnbn.core.dto.DigitalInstance;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceBuilder extends XmlBuilder {

    private final DigitalInstance instance;
    private final DigitalLibraryBuilder digLibBuilder;
    private final Long digLibId;
    private final DigitalDocumentBuilder digDocBuilder;

    public DigitalInstanceBuilder(DigitalInstance instance, DigitalLibraryBuilder digLibBuilder, DigitalDocumentBuilder digDocBuilder) {
        this.instance = instance;
        this.digLibBuilder = digLibBuilder;
        this.digLibId = null;
        this.digDocBuilder = digDocBuilder;
    }

    public DigitalInstanceBuilder(DigitalInstance instance, Long digLibId) {
        this.instance = instance;
        this.digLibBuilder = null;
        this.digLibId = digLibId;
        this.digDocBuilder = null;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalInstance", CZIDLO_NS);
        root.addAttribute(new Attribute("id", instance.getId().toString()));
        root.addAttribute(new Attribute("active", instance.isActive().toString()));
        appendElementWithContentIfNotNull(root, instance.getUrl(), "url");
        appendElementWithContentIfNotNull(root, instance.getFormat(), "format");
        appendElementWithContentIfNotNull(root, instance.getAccessibility(), "accessibility");
        appendElementWithContentIfNotNull(root, digLibId, "digitalLibraryId");
        appendElementWithContentIfNotNull(root, instance.getCreated(), "created");
        appendElementWithContentIfNotNull(root, instance.getDeactivated(), "deactivated");
        appendBuilderResultfNotNull(root, digLibBuilder);
        appendBuilderResultfNotNull(root, digDocBuilder);
        return root;
    }
}
