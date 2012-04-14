/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigDocIdentifier;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.NotDefinedException;
import cz.nkp.urnnbn.rest.exceptions.RestException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
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
            RegistrarScopeIdentifiersBuilder builder = digRepIdentifiersBuilder(doc.getId());
            return builder.buildDocument().toXML();
        } catch (RestException e) {
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
        DigDocIdType idType = Parser.parseDigRepIdType(idTypeStr);
        try {
            List<DigDocIdentifier> identifiers = dataAccessService().digDocIdentifiersByDigDocId(doc.getId());
            for (DigDocIdentifier id : identifiers) {
                if (id.getType().equals(idType)) {
                    RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(id);
                    return builder.buildDocument().toXML();
                }
            }
            throw new NotDefinedException(idType);
        } catch (RestException e) {
            throw e;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }

    @PUT
    @Path("/{idType}")
    @Produces("application/xml")
    public Response setOrUpdateIdentifierValue(@Context HttpServletRequest req,
            @PathParam("idType") String idTypeStr, String value) {
        String login = req.getRemoteUser();
        DigDocIdType idType = Parser.parseDigRepIdType(idTypeStr);
        try {
            DigDocIdentifier oldId = presentIdentifierOrNull(idType);
            if (oldId == null) { //insert new value
                DigDocIdentifier newId = addNewIdentifier(idType, value, login);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId).buildDocument().toXML();
                return Response.created(null).entity(responseXml).build();
            } else { //update value
                DigDocIdentifier newId = updateIdentifier(idType, value);
                String responseXml = new RegistrarScopeIdentifierBuilder(newId, oldId.getValue()).buildDocument().toXML();
                return Response.ok().entity(responseXml).build();
            }
        } catch (RestException e) {
            throw e;
        } catch (Throwable e) {
            throw new InternalException(e);
        }
    }

    private DigDocIdentifier presentIdentifierOrNull(DigDocIdType idType) {
        try {
            List<DigDocIdentifier> idList = dataAccessService().digDocIdentifiersByDigDocId(doc.getId());
            for (DigDocIdentifier id : idList) {
                if (id.getType().equals(idType)) {
                    return id;
                }
            }
            return null;
        } catch (DatabaseException ex) {
            throw new InternalException(ex);
        }
    }

    private DigDocIdentifier addNewIdentifier(DigDocIdType idType, String value, String login) {
        try {
            DigDocIdentifier newId = identifierInstance(idType, value);
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
            throw new InvalidDigDocIdentifier(ex.getMessage());
        }
    }

    private DigDocIdentifier identifierInstance(DigDocIdType idType, String value) {
        DigDocIdentifier result = new DigDocIdentifier();
        result.setDigDocId(doc.getId());
        result.setRegistrarId(doc.getRegistrarId());
        result.setType(idType);
        result.setValue(value);
        return result;
    }

    private DigDocIdentifier updateIdentifier(DigDocIdType idType, String value) {
        try {
            DigDocIdentifier id = identifierInstance(idType, value);
            dataUpdateService().updateDigRepIdentifier(id);
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
            throw new InvalidDigDocIdentifier(ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers() {
        try {
            RegistrarScopeIdentifiersBuilder builder = digRepIdentifiersBuilder(doc.getId());
            dataRemoveService().removeDigitalDocumentIdentifiers(doc.getId());
            return builder.buildDocument().toXML();
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
