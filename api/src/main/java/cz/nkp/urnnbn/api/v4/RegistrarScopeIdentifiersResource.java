/*
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
package cz.nkp.urnnbn.api.v4;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v4.exceptions.NotDefinedException;
import cz.nkp.urnnbn.api.v4.exceptions.RegistrarScopeIdentifierCollision;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownRegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifiersBuilder;

public class RegistrarScopeIdentifiersResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(RegistrarScopeIdentifiersResource.class.getName());

    private final DigitalDocument doc;

    public RegistrarScopeIdentifiersResource(DigitalDocument doc) {
        this.doc = doc;
    }

    @GET
    @Produces("application/xml")
    public String getRegistrarScopeIdentifiers() {
        try {
            return getRegistrarScopeIdentifiersXmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String getRegistrarScopeIdentifiersXmlRecord() {
        RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    @GET
    @Path("/{idType}")
    @Produces("application/xml")
    public String getRegistrarScopeIdentifierValue(@PathParam("idType") String idTypeStr) {
        try {
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            return getRegistrarScopeIdentifierXmlRecord(idType);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String getRegistrarScopeIdentifierXmlRecord(RegistrarScopeIdType idType) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(doc.getId());
        for (RegistrarScopeIdentifier id : identifiers) {
            if (id.getType().equals(idType)) {
                RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(id);
                return builder.buildDocumentWithResponseHeader().toXML();
            }
        }
        throw new NotDefinedException(idType);
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
            RegistrarScopeIdentifier oldId = getPresentIdentifierOrNull(idType);
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
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private RegistrarScopeIdentifier getPresentIdentifierOrNull(RegistrarScopeIdType idType) {
        try {
            return dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            return null;
        }
    }

    private RegistrarScopeIdentifier addNewIdentifier(RegistrarScopeIdType type, RegistrarScopeIdValue value, String login) {
        try {
            RegistrarScopeIdentifier newId = identifierInstance(type, value);
            dataImportService().addRegistrarScopeIdentifier(newId, login);
            return newId;
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new RegistrarScopeIdentifierCollision(ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier updateIdentifier(String login, RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        try {
            RegistrarScopeIdentifier id = identifierInstance(type, value);
            dataUpdateService().updateRegistrarScopeIdentifier(login, id);
            return id;
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            // LOGGER.log(Level.SEVERE, null, ex);
            throw new RegistrarScopeIdentifierCollision(ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier identifierInstance(RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        RegistrarScopeIdentifier result = new RegistrarScopeIdentifier();
        result.setDigDocId(doc.getId());
        result.setRegistrarId(doc.getRegistrarId());
        result.setType(type);
        result.setValue(value);
        return result;
    }

    @DELETE
    @Path("/{idType}")
    @Produces("application/xml")
    public String deleteRegistrarScopeIdentifier(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return deleteRegistrarScopeIdentifierWithXmlResponse(login, idTypeStr);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String deleteRegistrarScopeIdentifierWithXmlResponse(String login, String idTypeStr) {
        try {
            // builder before deleted
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            RegistrarScopeIdentifier identifier = dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
            RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(identifier);
            // delete
            dataRemoveService().removeRegistrarScopeIdentifier(doc.getId(), idType, login);
            // returned data before deleted
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            throw new UnknownRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers(@Context HttpServletRequest req) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return deleteAllRegistrarScopeIdentifiersWithXmlResponse(login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String deleteAllRegistrarScopeIdentifiersWithXmlResponse(String login) {
        try {
            // builder before deleted
            RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
            // delete
            dataRemoveService().removeRegistrarScopeIdentifiers(doc.getId(), login);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
