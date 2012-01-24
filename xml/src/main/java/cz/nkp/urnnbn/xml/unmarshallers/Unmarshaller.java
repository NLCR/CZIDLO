/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import java.util.logging.Level;
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
    static final XPathContext context = new XPathContext("resolver", "http://resolver.nkp.cz/v2/");
    private final Document doc;

    public Unmarshaller(Document doc) {
        this.doc = doc;
    }

    Node selectSingleNodeFromDoc(String xpath) {
        return selectSingleNode(xpath, doc);
    }

    Node selectSingleNode(String xpath, ParentNode parent) {
        Node result = selectSingleNodeOrNull(xpath, parent);
        if (result == null) {
            throw new RuntimeException("didn't find single node by xpath '" + xpath + "'");
        } else {
            return result;
        }
    }

    Node selectSingleNodeOrNullFromdoc(String xpath) {
        return selectSingleNodeOrNull(xpath, doc);
    }

    Node selectSingleNodeOrNull(String xpath, ParentNode parent) {
        logger.log(Level.SEVERE, "xpath: {0}", xpath);
        Nodes result = parent.query(xpath, context);
        if (result.size() != 1) {
            return null;
        }
        return result.get(0);
    }

    Nodes selectNodes(String xpath, ParentNode parent) {
        return parent.query(xpath, context);
    }

    String elementContentOrNull(String elementName, ParentNode parent) {
        String xpath = "resolver:" + elementName;
        logger.log(Level.SEVERE, "xpath: {0}", xpath);
        Node node = selectSingleNodeOrNull(xpath, parent);
        if (node != null && (node instanceof Element)) {
            return node.getValue();
        } else {
            return null;
        }
    }
}
