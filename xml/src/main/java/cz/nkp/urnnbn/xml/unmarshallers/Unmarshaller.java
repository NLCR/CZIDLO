/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.xml.commons.Xpath;
import java.util.logging.Logger;
import nu.xom.Document;
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

    private static final Logger logger = Logger.getLogger(Unmarshaller.class.getName());
    private static final String NS_PREFIX_NAME = "resolver";
    private static final String NS_PREFIX = NS_PREFIX_NAME + ":";
    static final XPathContext context = new XPathContext(NS_PREFIX_NAME, "http://resolver.nkp.cz/v2/");
    private final Document doc;

    static String prefixed(String elementName) {
        return NS_PREFIX + elementName;
    }

    public Unmarshaller(Document doc) {
        this.doc = doc;
    }

    Node selectSingleNodeFromDoc(String xpath) {
        return selectSingleNode(xpath, doc);
    }

    Node selectSingleNode(String xpath, ParentNode parent) {
        Node result = selectSingleElementOrNull(xpath, parent);
        if (result == null) {
            throw new RuntimeException("didn't find single node by xpath '" + xpath + "'");
        } else {
            return result;
        }
    }

    Element selectSingleElementOrNullFromdoc(Xpath xpath) {
        return (Element) selectSingleNodeOrNull(xpath, doc);
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
        Node node = selectSingleElementOrNull(elementName, parent);
        if (node != null && (node instanceof Element)) {
            return node.getValue();
        } else {
            return null;
        }
    }
}
