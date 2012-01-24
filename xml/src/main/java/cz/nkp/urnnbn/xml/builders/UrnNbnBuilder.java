/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnBuilder extends XmlBuilder {

    private final UrnNbnWithStatus urnWithStatus;

    public UrnNbnBuilder(UrnNbnWithStatus urnWithStatus) {
        this.urnWithStatus = urnWithStatus;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("urnNbn", RESOLVER);
        UrnNbn urn = urnWithStatus.getUrn();
        appendElementWithContentIfNotNull(root, urnWithStatus.getStatus().name(), "status");
        appendElementWithContentIfNotNull(root, urn.getCreated(), "created");
        appendElementWithContentIfNotNull(root, urn.getRegistrarCode(), "registrarSigla");
        appendElementWithContentIfNotNull(root, urn.toString(), "value");
        appendElementWithContentIfNotNull(root, urn.getDigRepId(), "digitalRepresentationId");
        return root;
    }
}
