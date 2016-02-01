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
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 * 
 * @author Martin Řehánek
 */
public abstract class AbstractUrnNbnResource extends Resource {

    public abstract String getUrnNbnXmlRecord(String urnNbnString);

    protected final UrnNbnWithStatus getUrnNbnWithStatus(String urnNbnString) {
        UrnNbn urnParsed = Parser.parseUrn(urnNbnString);
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(), urnParsed.getDocumentCode(), true);
    }

    protected final UrnNbnWithStatus getUrnNbnWithStatus(UrnNbn urn) {
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), true);
    }

}
