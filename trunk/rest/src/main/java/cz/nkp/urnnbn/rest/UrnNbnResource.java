/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
                    urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(), urnParsed.getDocumentCode());
            return new UrnNbnBuilder(urnWithStatus).buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
