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

import cz.nkp.urnnbn.api.v3.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarsBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractRegistrarsResource extends Resource {

    public String getRegistrarsApiV3XmlRecord(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<RegistrarBuilder> registrarBuilders = registrarBuilderList(addDigitalLibraries, addCatalogs);
        RegistrarsBuilder builder = new RegistrarsBuilder(registrarBuilders);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private List<RegistrarBuilder> registrarBuilderList(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilder> result = new ArrayList<RegistrarBuilder>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(registrarBuilder(registrar, addDigitalLibraries, addCatalogs));
        }
        return result;
    }

    protected Registrar registrarFromRegistarCode(String registrarCodeStr) {
        RegistrarCode registrarCode = Parser.parseRegistrarCode(registrarCodeStr);
        Registrar registrar = dataAccessService().registrarByCode(registrarCode);
        if (registrar == null) {
            throw new UnknownRegistrarException(registrarCode);
        } else {
            return registrar;
        }
    }
}
