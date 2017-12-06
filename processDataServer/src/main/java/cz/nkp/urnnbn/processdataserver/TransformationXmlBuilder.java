/*
 * Copyright (C) 2013 Martin Řehánek
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
package cz.nkp.urnnbn.processdataserver;

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;

/**
 * @author Martin Řehánek
 */
public class TransformationXmlBuilder {

    private final XmlTransformation transformation;

    public TransformationXmlBuilder(XmlTransformation transformation) {
        this.transformation = transformation;
    }

    public String buildXml() {
        StringBuilder result = new StringBuilder();
        result.append("<transformation>");
        appendElementIfContentNotNull(result, "id", transformation.getId());
        appendElementIfContentNotNull(result, "type", transformation.getType());
        appendElementIfContentNotNull(result, "name", transformation.getName());
        appendElementIfContentNotNull(result, "description", transformation.getDescription());
        appendElementIfContentNotNull(result, "created", transformation.getCreated());
        result.append("</transformation>");
        return result.toString();
    }

    private void appendElementIfContentNotNull(StringBuilder result, String elementName, Object content) {
        if (content != null) {
            result.append('<').append(elementName).append('>');
            result.append(content.toString());
            result.append("</").append(elementName).append('>');
        }
    }
}
