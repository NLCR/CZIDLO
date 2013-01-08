/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.exceptions.ApiException;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidRegistrarScopeIdentifier;
import cz.nkp.urnnbn.api.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.exceptions.NotDefinedException;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import java.util.List;
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
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentIdentifiersResource extends Resource {

    @Context
    private UriInfo context;
    private final DigitalDocument doc;

    public DigitalDocumentIdentifiersResource(DigitalDocument doc) {
        this.doc = doc;
    }

    @GET
    @Produces("application/xml")
    public String getIdentifiers() {
        try {
            RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (ApiException e) {
            throw e;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }

    @GET
    @Path("/{idType}")
    @Produces("application/xml")
    public String getIdentifier(@PathParam("idType") String idTypeStr) {
        RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
        try {
            List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(doc.getId());
            for (RegistrarScopeIdentifier id : identifiers) {
                if (id.getType().equals(idType)) {
                    RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(id);
                    return builder.buildDocumentWithResponseHeader().toXML();
                }
            }
            throw new NotDefinedException(idType);
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @PUT
    @Path("/{idType}")
    @Produces("application/xml")
    public Response setOrUpdateIdentifierValue(@Context HttpServletRequest req,
            @PathParam("idType") String idTypeStr, String value) {
        String login = req.getRemoteUser();
        RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
        try {
            RegistrarScopeIdentifier oldId = presentIdentifierOrNull(idType);
            if (oldId == null) { //insert new value
                RegistrarScopeIdentifier newId = addNewIdentifier(idType, value, login);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId).buildDocumentWithResponseHeader().toXML();
                return Response.created(null).entity(responseXml).build();
            } else { //update value
                RegistrarScopeIdentifier newId = updateIdentifier(idType, value);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId, oldId.getValue()).buildDocumentWithResponseHeader().toXML();
                return Response.ok().entity(responseXml).build();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @DELETE
    @Path("/{idType}")
    @Produces("application/xml")
    public String deleteIdentifier(@Context HttpServletRequest req,
            @PathParam("idType") String idTypeStr) {
        String login = req.getRemoteUser();
        try {
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            RegistrarScopeIdentifier identifier = dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
            dataRemoveService().removeDigitalDocumentId(doc.getId(), idType, login);
            RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(identifier);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            //should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier presentIdentifierOrNull(RegistrarScopeIdType idType) {
        try {
            return dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            return null;
        }
    }

    private RegistrarScopeIdentifier addNewIdentifier(RegistrarScopeIdType idType, String value, String login) {
        try {
            RegistrarScopeIdentifier newId = identifierInstance(idType, value);
            dataImportService().addRegistrarScopeIdentifier(newId, login);
            return newId;
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier identifierInstance(RegistrarScopeIdType idType, String value) {
        RegistrarScopeIdentifier result = new RegistrarScopeIdentifier();
        result.setDigDocId(doc.getId());
        result.setRegistrarId(doc.getRegistrarId());
        result.setType(idType);
        result.setValue(value);
        return result;
    }

    private RegistrarScopeIdentifier updateIdentifier(RegistrarScopeIdType idType, String value) {
        try {
            RegistrarScopeIdentifier id = identifierInstance(idType, value);
            dataUpdateService().updateDigDocIdentifier(id);
            return id;
        } catch (UnknownRegistrarException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers(@Context HttpServletRequest req) {
        String login = req.getRemoteUser();
        try {
            RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
            dataRemoveService().removeDigitalDocumentIdentifiers(doc.getId(), login);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            //should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
