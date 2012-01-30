/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigRepIdentifier;
import cz.nkp.urnnbn.rest.exceptions.NotDefinedException;
import cz.nkp.urnnbn.rest.exceptions.RestException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifiersBuilder;
import java.util.List;
import java.util.logging.Level;
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
public class DigitalRepresentationIdentifiersResource extends Resource {

    @Context
    private UriInfo context;
    private final DigitalRepresentation digRep;

    public DigitalRepresentationIdentifiersResource(DigitalRepresentation digRep) {
        this.digRep = digRep;
    }

    @GET
    @Produces("application/xml")
    public String getIdentifiers() {
        try {
            DigitalRepresentationIdentifiersBuilder builder = digRepIdentifiersBuilder(digRep.getId());
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
        DigRepIdType idType = Parser.parseDigRepIdType(idTypeStr);
        try {
            List<DigRepIdentifier> identifiers = dataAccessService().digRepIdentifiersByDigRepId(digRep.getId());
            for (DigRepIdentifier id : identifiers) {
                if (id.getType().equals(idType)) {
                    DigitalRepresentationIdentifierBuilder builder = new DigitalRepresentationIdentifierBuilder(id);
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
    public Response setOrUpdateIdentifierValue(@PathParam("idType") String idTypeStr, String value) {
        DigRepIdType idType = Parser.parseDigRepIdType(idTypeStr);
        try {
            DigRepIdentifier oldId = presentIdentifierOrNull(idType);
            if (oldId == null) { //insert new value
                DigRepIdentifier newId = addNewIdentifier(idType, value);
                String responseXml = new DigitalRepresentationIdentifierBuilder(newId).buildDocument().toXML();
                return Response.created(null).entity(responseXml).build();
            } else { //update value
                DigRepIdentifier newId = updateIdentifier(idType, value);
                String responseXml = new DigitalRepresentationIdentifierBuilder(newId, oldId.getValue()).buildDocument().toXML();
                return Response.ok().entity(responseXml).build();
            }
        } catch (RestException e) {
            throw e;
        } catch (Throwable e) {
            throw new InternalException(e);
        }
    }

    private DigRepIdentifier presentIdentifierOrNull(DigRepIdType idType) {
        try {
            List<DigRepIdentifier> idList = dataAccessService().digRepIdentifiersByDigRepId(digRep.getId());
            for (DigRepIdentifier id : idList) {
                if (id.getType().equals(idType)) {
                    return id;
                }
            }
            return null;
        } catch (DatabaseException ex) {
            throw new InternalException(ex);
        }
    }

    private DigRepIdentifier addNewIdentifier(DigRepIdType idType, String value) {
        try {
            DigRepIdentifier newId = identifierInstance(idType, value);
            dataImportService().addNewDigRepId(newId);
            return newId;
        } catch (UnknownRegistrarException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigRepException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            throw new InvalidDigRepIdentifier(ex.getMessage());
        }
    }

    private DigRepIdentifier identifierInstance(DigRepIdType idType, String value) {
        DigRepIdentifier result = new DigRepIdentifier();
        result.setDigRepId(digRep.getId());
        result.setRegistrarId(digRep.getRegistrarId());
        result.setType(idType);
        result.setValue(value);
        return result;
    }

    private DigRepIdentifier updateIdentifier(DigRepIdType idType, String value) {
        try {
            DigRepIdentifier id = identifierInstance(idType, value);
            dataUpdate().updateDigRepIdentifier(id);
            return id;
        } catch (UnknownRegistrarException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigRepException ex) {
            //should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            throw new InvalidDigRepIdentifier(ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers() {
        try {
            DigitalRepresentationIdentifiersBuilder builder = digRepIdentifiersBuilder(digRep.getId());
            dataRemoveService().removeDigitalRepresentationIdentifiers(digRep.getId());
            return builder.buildDocument().toXML();
        } catch (UnknownDigRepException ex) {
            //should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
