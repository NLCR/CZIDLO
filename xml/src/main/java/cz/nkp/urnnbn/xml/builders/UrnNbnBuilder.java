/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Element;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnBuilder extends XmlBuilder {

    private static final Logger logger = Logger.getLogger(UrnNbnBuilder.class.getName());
    private final UrnNbnWithStatus urnWithStatus;

    public UrnNbnBuilder(UrnNbnWithStatus urnWithStatus) {
        this.urnWithStatus = urnWithStatus;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("urnNbn", RESOLVER);
        UrnNbn urn = urnWithStatus.getUrn();
        appendTimestamps(root);
        appendElementWithContentIfNotNull(root, urnWithStatus.getStatus().name(), "status");
        appendElementWithContentIfNotNull(root, urn.getCreated(), "created");
        appendElementWithContentIfNotNull(root, urn.getRegistrarCode(), "registrarCode");
        appendElementWithContentIfNotNull(root, urn.toString(), "value");
        appendElementWithContentIfNotNull(root, urn.getDigDocId(), "digitalDocumentId");
        return root;
    }

    void appendTimestamps(Element rootElement) {
        UrnNbn urn = urnWithStatus.getUrn();
        DateTime created = urn.getCreated();
        if (created == null) {
            logger.log(Level.WARNING, "empty value of \"created\" for urn:nbn  {0}", urn.toString());
        } else {
            appendElementWithContentIfNotNull(rootElement, urn.getCreated(), "created");
        }
        DateTime modified = urn.getModified();
        if (modified == null) {
            logger.log(Level.WARNING, "empty value of \"modified\" for urn:nbn  {0}", urn.toString());
        } else if (!modified.equals(created)) {
            appendElementWithContentIfNotNull(rootElement, urn.getModified(), "modified");
        }
    }
}
