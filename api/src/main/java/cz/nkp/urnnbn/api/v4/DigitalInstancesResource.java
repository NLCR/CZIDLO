/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4;

import java.util.logging.Level;

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
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;

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
    public String getDigitalInstancesXmlRecord() {
        try {
            return super.getDigitalInstancesApiV4XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @Path("id/{digInstId}")
    @Override
    public DigitalInstanceResource getDigitalInstanceResource(@PathParam("digInstId") String digInstIdStr) {
        try {
            long id = Parser.parseDigInstId(digInstIdStr);
            DigitalInstance digitalInstance = getDigitalInstance(id);
            return new DigitalInstanceResource(digitalInstance);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(@Context HttpServletRequest req, String content) {
        try {
            checkServerNotReadOnly();
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document requestXmlData = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV4().loadDocument(content);
            DigitalInstance digitalInstance = digitalInstanceFromApiV4Document(requestXmlData);
            String response = super.addNewDigitalInstanceWithApiV4Response(digitalInstance, login);
            return Response.created(null).entity(response).build();
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
