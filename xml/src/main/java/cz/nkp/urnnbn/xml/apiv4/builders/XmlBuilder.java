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
package cz.nkp.urnnbn.xml.apiv4.builders;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.IdentifiableWithDatestamps;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Namespaces;
import cz.nkp.urnnbn.xml.config.XmlModuleConfiguration;

/**
 *
 * @author Martin Řehánek
 */
public abstract class XmlBuilder {

    private static final Logger logger = Logger.getLogger(XmlBuilder.class.getName());
    private static final boolean INCLUDE_SCHEMA = true;
    private static String responseSchema = null;
    static String CZIDLO_NS = Namespaces.CZIDLO_V4_NS;
    static String IDTYPE_INTERNAL = "INTERNAL";

    abstract Element buildRootElement();

    private static String getResponseSchema() {
        if (responseSchema == null) {
            responseSchema = Namespaces.XSI_NS + ' ' + XmlModuleConfiguration.instanceOf().getApiV4ResponseXsdLocation();
        }
        return responseSchema;
    }

    public Document buildDocumentWithResponseHeader() {
        Element response = new Element("response", CZIDLO_NS);
        if (INCLUDE_SCHEMA) {
            Attribute schemaLocation = new Attribute("xsi:schemaLocation", Namespaces.XSI_NS, getResponseSchema());
            response.addAttribute(schemaLocation);
        }
        response.appendChild(buildRootElement());
        return new Document(response);
    }

    public Document buildDocumentWithoutResponseHeader() {
        Element rootEl = buildRootElement();
        if (INCLUDE_SCHEMA) {
            Attribute schemaLocation = new Attribute("xsi:schemaLocation", Namespaces.XSI_NS, getResponseSchema());
            rootEl.addAttribute(schemaLocation);
        }
        return new Document(rootEl);
    }

    Element appendElement(Element root, String elementName) {
        Element child = new Element(elementName, CZIDLO_NS);
        root.appendChild(child);
        return child;
    }

    final Element appendElementWithContentIfNotNull(Element root, Object content, String elementName) {
        if (content != null) {
            Element child = new Element(elementName, CZIDLO_NS);
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
            logger.log(Level.WARNING, "empty value of \"created\" for {0}  with id {1}", new Object[] { entityName, entity.getId() });
        } else {
            appendElementWithContentIfNotNull(rootElement, entity.getCreated(), "created");
        }
        DateTime modified = entity.getModified();
        if (modified == null) {
            logger.log(Level.WARNING, "empty value of \"modified\" for {0}  with id {1}", new Object[] { entityName, entity.getId() });
        } else if (!modified.equals(created)) {
            appendElementWithContentIfNotNull(rootElement, entity.getModified(), "modified");
        }
    }

    protected void appendPredecessors(Element root, UrnNbn urn) {
        List<UrnNbnWithStatus> predecessors = urn.getPredecessors();
        if (predecessors != null && !predecessors.isEmpty()) {
            for (UrnNbnWithStatus predecessor : predecessors) {
                Element predecessorEl = new Element("predecessor", CZIDLO_NS);
                predecessorEl.addAttribute(new Attribute("value", predecessor.getUrn().toString()));
                if (predecessor.getNote() != null) {
                    predecessorEl.addAttribute(new Attribute("note", predecessor.getNote()));
                }
                root.appendChild(predecessorEl);
            }
        }
    }

    protected void appendSuccessors(Element root, UrnNbn urn) {
        List<UrnNbnWithStatus> successors = urn.getSuccessors();
        if (successors != null && !successors.isEmpty()) {
            for (UrnNbnWithStatus successor : successors) {
                Element predecessorEl = new Element("successor", CZIDLO_NS);
                predecessorEl.addAttribute(new Attribute("value", successor.getUrn().toString()));
                if (successor.getNote() != null) {
                    predecessorEl.addAttribute(new Attribute("note", successor.getNote()));
                }
                root.appendChild(predecessorEl);
            }
        }
    }

    protected Element appendUrnNbnElement(Element root, UrnNbn urnNbn) {
        Element predecessorEl = new Element("urnNbn", CZIDLO_NS);
        appendElementWithContentIfNotNull(predecessorEl, urnNbn, "value");
        root.appendChild(predecessorEl);
        return predecessorEl;
    }
}
