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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v4.exceptions.NotDefinedException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownRegistrarScopeIdentifierException;
import cz.nkp.urnnbn.api.v4.json.RegistrarScopeIdentifierBuilderJson;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierCollisionException;
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
    public Response getRegistrarScopeIdentifiers(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            switch (format) {
            case XML:
                String xml = registrarScopeIdentifiersBuilderXml(doc.getId()).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = registrarScopeIdentifiersBuilderJson(doc.getId()).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    @GET
    @Path("/{idType}")
    public Response getRegistrarScopeIdentifierValue(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("idType") String idTypeStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(format, idTypeStr);
        try {
            switch (format) {
            case XML:
                String xml = new RegistrarScopeIdentifierBuilder(findRsid(format, idType)).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = new RegistrarScopeIdentifierBuilderJson(findRsid(format, idType)).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private RegistrarScopeIdentifier findRsid(ResponseFormat format, RegistrarScopeIdType idType) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(doc.getId());
        for (RegistrarScopeIdentifier id : identifiers) {
            if (id.getType().equals(idType)) {
                return id;
            }
        }
        throw new NotDefinedException(format, idType);
    }

    @PUT
    @Path("/{idType}")
    @Produces("application/xml")
    public Response setOrUpdateIdentifierValue(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr, String idValueStr) {
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            String login = req.getRemoteUser();
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(format, idTypeStr);
            RegistrarScopeIdValue idValue = Parser.parseRegistrarScopeIdValue(format, idValueStr);
            RegistrarScopeIdentifier oldId = getPresentIdentifierOrNull(idType);
            if (oldId == null) { // insert new value
                RegistrarScopeIdentifier newId = addNewIdentifier(format, idType, idValue, login);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId).buildDocumentWithResponseHeader().toXML();
                return Response.created(null).entity(responseXml).build();
            } else { // update value
                RegistrarScopeIdentifier newId = updateIdentifier(format, login, idType, idValue);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId, oldId.getValue()).buildDocumentWithResponseHeader().toXML();
                return Response.ok().entity(responseXml).build();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private RegistrarScopeIdentifier getPresentIdentifierOrNull(RegistrarScopeIdType idType) {
        try {
            return dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            return null;
        }
    }

    private RegistrarScopeIdentifier addNewIdentifier(ResponseFormat format, RegistrarScopeIdType type, RegistrarScopeIdValue value, String login) {
        try {
            RegistrarScopeIdentifier newId = identifierInstance(type, value);
            dataImportService().addRegistrarScopeIdentifier(newId, login);
            return newId;
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new InternalException(format, ex);
        } catch (UnknownDigDocException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new InternalException(format, ex);
        } catch (RegistrarScopeIdentifierCollisionException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new cz.nkp.urnnbn.api.v4.exceptions.RegistrarScopeIdentifierCollisionException(format, ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier updateIdentifier(ResponseFormat format, String login, RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        try {
            RegistrarScopeIdentifier id = identifierInstance(type, value);
            dataUpdateService().updateRegistrarScopeIdentifier(login, id);
            return id;
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new InternalException(format, ex);
        } catch (UnknownDigDocException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new InternalException(format, ex);
        } catch (RegistrarScopeIdentifierCollisionException ex) {
            LOGGER.log(Level.FINE, null, ex);
            throw new cz.nkp.urnnbn.api.v4.exceptions.RegistrarScopeIdentifierCollisionException(format, ex.getMessage());
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
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            String login = req.getRemoteUser();
            return deleteRegistrarScopeIdentifierWithXmlResponse(format, login, idTypeStr);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private String deleteRegistrarScopeIdentifierWithXmlResponse(ResponseFormat format, String login, String idTypeStr) {
        try {
            // builder before deleted
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(format, idTypeStr);
            RegistrarScopeIdentifier identifier = dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
            RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(identifier);
            // delete
            dataRemoveService().removeRegistrarScopeIdentifier(doc.getId(), idType, login);
            // returned data before deleted
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(format, ex.getMessage());
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            throw new UnknownRegistrarScopeIdentifierException(format, ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers(@Context HttpServletRequest req) {
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            String login = req.getRemoteUser();
            return deleteAllRegistrarScopeIdentifiersWithXmlResponse(format, login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private String deleteAllRegistrarScopeIdentifiersWithXmlResponse(ResponseFormat format, String login) {
        try {
            // builder before deleted
            RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilderXml(doc.getId());
            // delete
            dataRemoveService().removeRegistrarScopeIdentifiers(doc.getId(), login);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(format, ex.getMessage());
        }
    }
}
