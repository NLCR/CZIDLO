/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.Archiver;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverBuilder extends XmlBuilder {

    private final Archiver archiver;

    public ArchiverBuilder(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("archiver", RESOLVER);
        appendIdentifierElement(root, IDTYPE_INTERNAL, archiver.getId());
        appendTimestamps(root, archiver, "archiver");
        appendElementWithContentIfNotNull(root, archiver.getName(), "name");
        appendElementWithContentIfNotNull(root, archiver.getDescription(), "description");
        return root;
    }
}
