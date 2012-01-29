/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalRepresentationException;
import cz.nkp.urnnbn.rest.exceptions.UnknownUrnException;
import java.util.logging.Level;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/resolver")
public class ResolverResource extends Resource {

    @Context
    private UriInfo context;

    @Path("{urn}")
    public Resource getDigitalRepresentationResource(@PathParam("urn") String urnPar) {
        try {
            UrnNbn urnParsed = Parser.parseUrn(urnPar);
            Sigla sigla = Sigla.valueOf(urnParsed.getRegistrarCode());
            UrnNbnWithStatus fetched = dataAccessService().urnBySiglaAndDocumentCode(sigla, urnParsed.getDocumentCode());
            switch (fetched.getStatus()) {
                case ACTIVE:
                    DigitalRepresentation rep = dataAccessService().digRepByInternalId(fetched.getUrn().getDigRepId());
                    if (rep == null) {
                        throw new UnknownDigitalRepresentationException(fetched.getUrn());
                    }
                    return new DigitalRepresentationResource(rep, fetched.getUrn());
                case FREE:
                    throw new UnknownUrnException(urnParsed);
                default: //booked and abandoned
                    throw new UnknownDigitalRepresentationException(fetched.getUrn());
            }
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
