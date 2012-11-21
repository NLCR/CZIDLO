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
        Element root = new Element("urnNbn", RESOLVER_NS);
        UrnNbn urn = urnWithStatus.getUrn();
        appendElementWithContentIfNotNull(root, urnWithStatus.getStatus().name(), "status");
        appendElementWithContentIfNotNull(root, urn.getRegistrarCode(), "registrarCode");
        appendElementWithContentIfNotNull(root, urn.toString(), "value");
        appendElementWithContentIfNotNull(root, urn.getDigDocId(), "digitalDocumentId");
        appendTimestamps(root);
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
