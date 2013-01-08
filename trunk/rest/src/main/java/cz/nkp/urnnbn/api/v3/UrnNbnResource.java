/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Martin Řehánek
 */
@Path("/urnnbn")
public class UrnNbnResource extends Resource {

    @GET
    @Path("{urn}")
    @Produces("text/xml")
    public String getUrnNbnXml(@PathParam("urn") String urnStr) {
        try {
            UrnNbn urnParsed = Parser.parseUrn(urnStr);
            UrnNbnWithStatus urnWithStatus = dataAccessService().
                    urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(), urnParsed.getDocumentCode(), true);
            return new UrnNbnBuilder(urnWithStatus).buildDocumentWithResponseHeader().toXML();
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
