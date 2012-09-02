    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.rest.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.unmarshallers.DigitalInstanceUnmarshaller;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author Martin Řehánek
 */
@Path("/digitalInstances")
public class DigitalInstancesResource extends Resource {

    private static final int MAX_URL_LENGTH = 200;
    private final DigitalDocument digDoc;

    public DigitalInstancesResource() {
        digDoc = null;
    }

    public DigitalInstancesResource(DigitalDocument digDoc) {
        this.digDoc = digDoc;
    }

    @GET
    @Produces("application/xml")
    public String getDigitalInstances() {
        try {
            DigitalInstancesBuilder builder = instancesBuilder();
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalInstancesBuilder instancesBuilder() throws DatabaseException {
        if (digDoc == null) {
            return new DigitalInstancesBuilder(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilder> instanceBuilders = instanceBuilders(digDoc);
            return new DigitalInstancesBuilder(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilder> instanceBuilders(DigitalDocument rep) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().instancesByDigDocId(rep.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            //todo: i knihovny atd.
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return result;
    }

    @Path("id/{digInstId}")
    public DigitalInstanceResource digitalInstance(@PathParam("digInstId") String digInstIdStr) {
        long id = Parser.parseDigInstId(digInstIdStr);
        try {
            DigitalInstance instance = dataAccessService().digInstanceByInternalId(id);
            if (instance == null) {
                throw new UnknownDigitalInstanceException(id);
            }
            return new DigitalInstanceResource(instance);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(
            @Context HttpServletRequest req,
            String content) {
        try {
            String login = req.getRemoteUser();
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            Document xmlDocument = validDocumentFromString(content, ApiModuleConfiguration.instanceOf().getInstanceImportSchema());
            DigitalInstance digInstFromClient = digitalInstanceFromDocument(xmlDocument);
            //TODO: this validation move to xsd
            Parser.parseUrl(digInstFromClient.getUrl(), MAX_URL_LENGTH);
            checkNoOtherDigInstInSameLibraryPresent(digInstFromClient);
            //actually import
            DigitalInstance digInstInserted = dataImportService().addDigitalInstance(digInstFromClient, login);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(digInstInserted, digInstFromClient.getLibraryId());
            String responseXml = builder.buildDocument().toXML();
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigLibException ex) {
            throw new UnknownDigitalLibraryException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InternalException(e);
        }
    }

    private DigitalInstance digitalInstanceFromDocument(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    private void checkNoOtherDigInstInSameLibraryPresent(DigitalInstance digInstFromClient) {
        try {
            List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digInstFromClient.getDigDocId());
            for (DigitalInstance instance : instances) {
                if (instance.getLibraryId().equals(digInstFromClient.getLibraryId())) {
                    throw new DigitalInstanceAlreadyPresentException(instance);
                }
            }
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
