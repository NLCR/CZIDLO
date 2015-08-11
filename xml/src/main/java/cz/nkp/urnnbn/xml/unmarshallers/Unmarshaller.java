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
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.xml.commons.Namespaces;
import cz.nkp.urnnbn.xml.commons.Xpath;
import cz.nkp.urnnbn.xml.unmarshallers.validation.ElementContentEnhancer;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XPathContext;

/**
 *
 * @author Martin Řehánek
 */
public abstract class Unmarshaller {

    static final Logger logger = Logger.getLogger(Unmarshaller.class.getName());
    private static final String NS_PREFIX_NAME = "resolver";
    private static final String NS_PREFIX = NS_PREFIX_NAME + ":";
    private static final XPathContext context = new XPathContext(NS_PREFIX_NAME, Namespaces.CZIDLO_NS);

    static String prefixed(String elementName) {
        return NS_PREFIX + elementName;
    }

    Node selectSingleNode(String xpath, ParentNode parent) {
        Node result = selectSingleElementOrNull(xpath, parent);
        if (result == null) {
            throw new RuntimeException("didn't find single node by xpath '" + xpath + "'");
        } else {
            return result;
        }
    }

    Element selectSingleElementOrNull(String elementName, ParentNode parent) {
        return (Element) selectSingleNodeOrNull(new Xpath(prefixed(elementName)), parent);
    }

    Node selectSingleNodeOrNull(Xpath xpath, ParentNode parent) {
        Nodes nodes = parent.query(xpath.toString(), context);
        if (nodes.size() != 1) {
            return null;
        }
        return nodes.get(0);
    }

    Nodes selectNodes(Xpath xpath, ParentNode parent) {
        return parent.query(xpath.toString(), context);
    }

    String elementContentOrNull(String elementName, ParentNode parent) {
        return elementContentOrNull(elementName, parent, null);
    }

    String elementContentOrNull(String elementName, ParentNode parent, ElementContentEnhancer enhancer) {
        Node node = selectSingleElementOrNull(elementName, parent);
        if (node != null && (node instanceof Element)) {
            String content = node.getValue();
            if (enhancer != null) {
                return enhancer.toEnhancedValueOrNull(content);
            } else {
                return content;
            }
        } else {
            return null;
        }
    }

    String attributeContentOrNull(String attributeName, ParentNode parent) {
        return attributeContentOrNull(attributeName, parent, null);
    }

    String attributeContentOrNull(String attributeName, ParentNode parent, ElementContentEnhancer enhancer) {
        if (parent == null) {
            return null;
        } else {
            Attribute attr = selectSingleAttributeOrNull(attributeName, parent);
            if (attr != null) {
                String content = attr.getValue();
                if (enhancer != null) {
                    return enhancer.toEnhancedValueOrNull(content);
                } else {
                    return content;
                }
            } else {
                return null;
            }
        }
    }

    Attribute selectSingleAttributeOrNull(String attributeNam, ParentNode parent) {
        return (Attribute) selectSingleNodeOrNull(new Xpath('@' + attributeNam), parent);
    }
}
