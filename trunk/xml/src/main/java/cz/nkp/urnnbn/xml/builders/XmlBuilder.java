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

import cz.nkp.urnnbn.core.dto.IdentifiableWithDatestamps;
import cz.nkp.urnnbn.xml.commons.Namespaces;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public abstract class XmlBuilder {

    private static final Logger logger = Logger.getLogger(XmlBuilder.class.getName());
    private static final String RESPONSE_SCHEMA = "http://resolver.nkp.cz/v2/ http://iris.mzk.cz/cache/resolver-response.xsd";
    private static final boolean INCLUDE_SCHEMA = true;
    static String RESOLVER = Namespaces.RESOLVER;
    static String IDTYPE_INTERNAL = "INTERNAL";

    abstract Element buildRootElement();

    public Document buildDocument() {
        Element rootElement = buildRootElement();
        if (INCLUDE_SCHEMA) {
            Attribute schemaLocation = new Attribute("xsi:schemaLocation", Namespaces.XSI, RESPONSE_SCHEMA);
            rootElement.addAttribute(schemaLocation);
        }
        return new Document(rootElement);
    }

    Element appendElement(Element root, String elementName) {
        Element child = new Element(elementName, RESOLVER);
        root.appendChild(child);
        return child;
    }

    void appendIdentifierElement(Element root, String idType, Object value) {
        Element id = appendElementWithContentIfNotNull(root, value, "id");
        id.addAttribute(new Attribute("type", idType));
    }

    final Element appendElementWithContentIfNotNull(Element root, Object content, String elementName) {
        if (content != null) {
            Element child = new Element(elementName, RESOLVER);
            root.appendChild(child);
            child.appendChild(String.valueOf(content));
            return child;
        } else {
            return null;
        }
    }

    final void appendBuilderResultfNotNull(Element root, XmlBuilder builder) {
        if (builder != null) {
            root.appendChild(builder.buildRootElement());
        }
    }

    final void appendTimestamps(Element rootElement, IdentifiableWithDatestamps entity, String entityName) {
        DateTime created = entity.getCreated();
        if (created == null) {
            logger.log(Level.WARNING, "empty value of \"created\" for {0}  with id {1}", new Object[]{entityName, entity.getId()});
        } else {
            appendElementWithContentIfNotNull(rootElement, entity.getCreated(), "created");
        }
        DateTime modified = entity.getModified();
        if (modified == null) {
            logger.log(Level.WARNING, "empty value of \"modified\" for {0}  with id {1}", new Object[]{entityName, entity.getId()});
        } else if (!modified.equals(created)) {
            appendElementWithContentIfNotNull(rootElement, entity.getModified(), "modified");
        }
    }
}
