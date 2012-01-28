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
import cz.nkp.urnnbn.rest.exceptions.NotDefinedException;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifiersBuilder;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationIdentifiersResource extends Resource {

    @Context
    private UriInfo context;
    private final DigitalRepresentation rep;

    public DigitalRepresentationIdentifiersResource(DigitalRepresentation rep) {
        this.rep = rep;
    }

    @GET
    @Produces("application/xml")
    public String getIdentifiers() {
        try {
//            List<DigRepIdentifier> identifiers = dataAccessService().digRepIdentifiersByDigRepId(rep.getId());
            DigitalRepresentationIdentifiersBuilder builder = digRepIdentifiersBuilder(rep.getId());
            //DigitalRepresentationIdentifiersBuilder builder = new DigitalRepresentationIdentifiersBuilder(di);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @GET
    @Path("/{idType}")
    @Produces("application/xml")
    public String getIdentifier(@PathParam("idType") String idTypeStr) {
        DigRepIdType idType = Parser.parseDigRepIdType(idTypeStr);
        try {
            List<DigRepIdentifier> identifiers = dataAccessService().digRepIdentifiersByDigRepId(rep.getId());
            for (DigRepIdentifier id : identifiers) {
                if (id.getType().equals(idType)) {
                    DigitalRepresentationIdentifierBuilder builder = new DigitalRepresentationIdentifierBuilder(id);
                    return builder.buildDocument().toXML();
                }
            }
            throw new NotDefinedException(idType);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @PUT
    @Consumes("application/xml")
    @Produces("application/xml")
    public String addNewIdentifier(String content) {
        return "<result>" + content + "</result>";
    }
}
