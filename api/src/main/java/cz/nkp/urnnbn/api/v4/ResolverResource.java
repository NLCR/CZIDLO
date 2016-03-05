/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4;

import java.util.logging.Level;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownUrnException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/resolver")
public class ResolverResource extends ApiV4Resource {

    @Path("{urn}")
    public Resource getDigitalDocumentResource(@PathParam("urn") String urnPar) {
        try {
            UrnNbn urnParsed = Parser.parseUrn(urnPar);
            UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(),
                    urnParsed.getDocumentCode(), true);
            switch (fetched.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                DigitalDocument doc = dataAccessService().digDocByInternalId(fetched.getUrn().getDigDocId());
                if (doc == null) {
                    throw new UnknownDigitalDocumentException(fetched.getUrn());
                } else {
                    // update resolvations statistics
                    statisticService().incrementResolvationStatistics(urnParsed.getRegistrarCode().toString());
                    return new DigitalDocumentResource(doc, fetched.getUrn());
                }
            case FREE:
                throw new UnknownUrnException(urnParsed);
            case RESERVED:
                throw new UnknownDigitalDocumentException(fetched.getUrn());
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e.getMessage());
        }
    }
}
