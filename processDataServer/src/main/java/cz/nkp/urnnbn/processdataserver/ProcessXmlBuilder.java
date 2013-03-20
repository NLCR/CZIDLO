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

import cz.nkp.urnnbn.processmanager.core.Process;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessXmlBuilder {

    private final Process process;

    public ProcessXmlBuilder(Process process) {
        this.process = process;
    }

    public String buildXml() {
        StringBuilder result = new StringBuilder();
        result.append("<process>");
        appendElementIfContentNotNull(result, "id", process.getId());
        appendElementIfContentNotNull(result, "type", process.getType());
        appendElementIfContentNotNull(result, "state", process.getState());
        appendElementIfContentNotNull(result, "owner", process.getOwnerLogin());
        appendElementIfContentNotNull(result, "scheduled", process.getScheduled());
        appendElementIfContentNotNull(result, "started", process.getStarted());
        appendElementIfContentNotNull(result, "finished", process.getFinished());
        result.append("</process>");
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
