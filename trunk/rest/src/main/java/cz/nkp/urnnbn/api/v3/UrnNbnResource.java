/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractUrnNbnResource;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
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
public class UrnNbnResource extends AbstractUrnNbnResource {

    @GET
    @Path("{urn}")
    @Produces("text/xml")
    @Override
    public String getUrnNbnXmlRecord(@PathParam("urn") String urnNbnString) {
        try {
            UrnNbnWithStatus urnNbnWithStatus = getUrnNbnWithStatus(urnNbnString);
            return new UrnNbnBuilder(urnNbnWithStatus).buildDocumentWithResponseHeader().toXML();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
