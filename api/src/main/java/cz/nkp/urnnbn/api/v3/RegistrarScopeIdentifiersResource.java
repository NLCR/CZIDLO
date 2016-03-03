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
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractRegistrarScopeIdentifiersResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifierBuilder;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifiersResource extends AbstractRegistrarScopeIdentifiersResource {

    public RegistrarScopeIdentifiersResource(DigitalDocument doc) {
        super(doc);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getRegistrarScopeIdentifiersXmlRecord() {
        try {
            return getRegistrarScopeIdentifiersApiV3XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @GET
    @Path("/{idType}")
    @Produces("application/xml")
    @Override
    public String getRegistrarScopeIdentifierValue(@PathParam("idType") String idTypeStr) {
        try {
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            return super.getRegistrarScopeIdentifierValueApiV3XmlRecord(idType);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @PUT
    @Path("/{idType}")
    @Produces("application/xml")
    public Response setOrUpdateIdentifierValue(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr, String idValueStr) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            RegistrarScopeIdValue idValue = Parser.parseRegistrarScopeIdValue(idValueStr);
            RegistrarScopeIdentifier oldId = presentIdentifierOrNull(idType);
            if (oldId == null) { // insert new value
                RegistrarScopeIdentifier newId = addNewIdentifier(idType, idValue, login);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId).buildDocumentWithResponseHeader().toXML();
                return Response.created(null).entity(responseXml).build();
            } else { // update value
                RegistrarScopeIdentifier newId = updateIdentifier(login, idType, idValue);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId, oldId.getValue()).buildDocumentWithResponseHeader().toXML();
                return Response.ok().entity(responseXml).build();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @DELETE
    @Path("/{idType}")
    @Produces("application/xml")
    public String deleteRegistrarScopeIdentifier(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return super.deleteRegistrarScopeIdentifierWithApiV3Response(login, idTypeStr);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers(@Context HttpServletRequest req) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return super.deleteAllRegistrarScopeIdentifiersWithApiV3Response(login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
