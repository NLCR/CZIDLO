/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v2;

import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v2_v3.AbstractUrnNbnResource;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.xml.apiv3.builders.UrnNbnBuilder;

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
            try {
                UrnNbnWithStatus urnNbnWithStatus = getUrnNbnWithStatus(urnNbnString);
                String apiV3XmlRecord = new UrnNbnBuilder(urnNbnWithStatus).buildDocumentWithResponseHeader().toXML();
                return ApiModuleConfiguration.instanceOf().getGetUrnNbnResponseV3ToV2Transformer().transform(apiV3XmlRecord).toXML();
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new InternalException(e);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }
}
