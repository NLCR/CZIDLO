/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.xml.commons.Namespaces;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public abstract class XmlBuilder {

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

    Element addElement(Element root, String elementName) {
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
//    final Element toDetachedRootElement() {
//        Element root = this.buildRootElement().getRootElement();
//        root.detach();
//        return root;
//    }
}
