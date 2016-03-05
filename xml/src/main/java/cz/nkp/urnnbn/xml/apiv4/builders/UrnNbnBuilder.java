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
package cz.nkp.urnnbn.xml.apiv4.builders;

import java.util.logging.Logger;

import nu.xom.Element;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;

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
        Element root = new Element("urnNbn", CZIDLO_NS);
        UrnNbn urn = urnWithStatus.getUrn();
        appendElementWithContentIfNotNull(root, urnWithStatus.getStatus().name(), "status");
        appendElementWithContentIfNotNull(root, urn.getDeactivationNote(), "deactivationNote");
        appendElementWithContentIfNotNull(root, urn.toString(), "value");
        appendElementWithContentIfNotNull(root, CountryCode.getCode(), "countryCode");
        appendElementWithContentIfNotNull(root, urn.getRegistrarCode(), "registrarCode");
        appendElementWithContentIfNotNull(root, urn.getDocumentCode(), "documentCode");
        appendElementWithContentIfNotNull(root, urn.getDigDocId(), "digitalDocumentId");
        appendTimestamps(root);
        appendPredecessors(root, urn);
        appendSuccessors(root, urn);
        return root;
    }

    void appendTimestamps(Element rootElement) {
        UrnNbn urn = urnWithStatus.getUrn();
        appendElementWithContentIfNotNull(rootElement, urn.getReserved(), "reserved");
        appendElementWithContentIfNotNull(rootElement, urn.getRegistered(), "registered");
        appendElementWithContentIfNotNull(rootElement, urn.getDeactivated(), "deactivated");
    }
}
