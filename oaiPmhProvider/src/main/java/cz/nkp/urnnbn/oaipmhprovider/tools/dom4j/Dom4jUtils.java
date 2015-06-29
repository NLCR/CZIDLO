/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;

/**
 *
 * @author Martin Řehánek
 */
public class Dom4jUtils {

    /**
     * Creates Xpath with prefixes from class Namespaces
     * @param expression
     * @return
     */
    public static XPath createXPath(String expression) {
        XPath result = DocumentHelper.createXPath(expression);
        result.setNamespaceURIs(Namespaces.getPrefixUriMap());
        return result;
    }

    public static Document loadDocument(String in, boolean validate) throws DocumentException {
        return DocumentLoader.loadDocument(in, validate);
    }
}
