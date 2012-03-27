/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalDocumentException;
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
    public Resource getDigitalDocumentResource(@PathParam("urn") String urnPar) {
        try {
            UrnNbn urnParsed = Parser.parseUrn(urnPar);
            UrnNbnWithStatus fetched = dataAccessService().
                    urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(), urnParsed.getDocumentCode());
            switch (fetched.getStatus()) {
                case ACTIVE:
                    DigitalDocument rep = dataAccessService().digDocByInternalId(fetched.getUrn().getDigDocId());
                    if (rep == null) {
                        throw new UnknownDigitalDocumentException(fetched.getUrn());
                    }
                    return new DigitalDocumentResource(rep, fetched.getUrn());
                case FREE:
                    throw new UnknownUrnException(urnParsed);
                default: //booked and abandoned
                    throw new UnknownDigitalDocumentException(fetched.getUrn());
            }
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
