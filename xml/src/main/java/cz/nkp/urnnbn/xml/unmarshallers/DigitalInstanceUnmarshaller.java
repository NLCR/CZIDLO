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

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.xml.unmarshallers.validation.LimitedLengthEnhancer;
import cz.nkp.urnnbn.xml.unmarshallers.validation.UrlValidator;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceUnmarshaller extends Unmarshaller {

    private final Document doc;

    public DigitalInstanceUnmarshaller(Document doc) {
        this.doc = doc;
    }

    public DigitalInstance getDigitalInstance() {
        DigitalInstance result = new DigitalInstance();
        Element root = doc.getRootElement();
        result.setUrl(elementContentOrNull("url", root, new UrlValidator(200)));
        result.setLibraryId(Long.valueOf(elementContentOrNull("digitalLibraryId", root, null)));
        result.setFormat(elementContentOrNull("format", root, new LimitedLengthEnhancer(50)));
        result.setAccessibility(elementContentOrNull("accessibility", root, new LimitedLengthEnhancer(50)));
        result.setActive(Boolean.TRUE);
        return result;
    }
}
