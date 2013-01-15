    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractDigitalInstancesResource;
import cz.nkp.urnnbn.api.DigitalInstanceResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
@Path("/digitalInstances")
public class DigitalInstancesResource extends AbstractDigitalInstancesResource {

    public DigitalInstancesResource() {
        super(null);
    }

    public DigitalInstancesResource(DigitalDocument digDoc) {
        super(digDoc);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalInstances() {
        return super.getDigitalInstances();
    }

    @Path("id/{digInstId}")
    public DigitalInstanceResource getDigitalInstanceRestource(@PathParam("digInstId") String digInstIdStr) {
        long id = Parser.parseDigInstId(digInstIdStr);
        return getDetdigitalInstanceResource(id);
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(
            @Context HttpServletRequest req, String content) {
        try {
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document xmlDocument = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV3().loadDocument(content);
            DigitalInstance digitalInstance = digitalInstanceFromDocument(xmlDocument);
            return super.addNewDigitalInstance(digitalInstance, login);
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            throw new InternalException(e);
        }
    }
}
