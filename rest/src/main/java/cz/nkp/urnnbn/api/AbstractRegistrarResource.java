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

import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.logging.Level;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractRegistrarResource extends Resource {

    protected final Registrar registrar;

    public AbstractRegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    protected String getRegistrarXml(boolean addDigitalLibraries, boolean addCatalogs) {
        try {
            RegistrarBuilder builder = registrarBuilder(registrar, addDigitalLibraries, addCatalogs);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (DatabaseException ex) {
            //TODO: rid of DatabaseException here
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
