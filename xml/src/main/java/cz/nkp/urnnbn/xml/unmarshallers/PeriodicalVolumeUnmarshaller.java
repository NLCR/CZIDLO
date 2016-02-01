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
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.xml.unmarshallers.validation.CcnbEnhancer;
import cz.nkp.urnnbn.xml.unmarshallers.validation.IssnEnhancer;
import cz.nkp.urnnbn.xml.unmarshallers.validation.LimitedLengthEnhancer;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class PeriodicalVolumeUnmarshaller extends IntelectualEntityUnmarshaller {

    public PeriodicalVolumeUnmarshaller(Element entityEl) {
        super(entityEl);
    }

    @Override
    public List<IntEntIdentifier> getIntEntIdentifiers() {
        List<IntEntIdentifier> result = new ArrayList<IntEntIdentifier>();
        Element titleInfoElement = selectSingleElementOrNull("titleInfo", entityEl);
        if (titleInfoElement == null) {
            logger.severe("missing element titleInfo");
        } else {
            appendId(result, identifierByElementName(titleInfoElement, "periodicalTitle", IntEntIdType.TITLE, true, new LimitedLengthEnhancer(100)));
            appendId(result,
                    identifierByElementName(titleInfoElement, "volumeTitle", IntEntIdType.VOLUME_TITLE, false, new LimitedLengthEnhancer(50)));
        }
        appendId(result, identifierByElementName(entityEl, "ccnb", IntEntIdType.CCNB, false, new CcnbEnhancer()));
        appendId(result, identifierByElementName(entityEl, "issn", IntEntIdType.ISSN, false, new IssnEnhancer()));
        appendId(result, identifierByElementName(entityEl, "otherId", IntEntIdType.OTHER, false, new LimitedLengthEnhancer(50)));
        return result;
    }
}
