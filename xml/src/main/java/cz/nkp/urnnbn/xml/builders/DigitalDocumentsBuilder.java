/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentsBuilder extends XmlBuilder {

    private final int digRepCount;

    public DigitalDocumentsBuilder(int digRepCount) {
        this.digRepCount = digRepCount;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalRepresentations", RESOLVER);
        appendElementWithContentIfNotNull(root, digRepCount, "count");
        return root;
    }
}
