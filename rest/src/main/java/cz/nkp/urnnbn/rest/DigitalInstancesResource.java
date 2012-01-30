    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigiLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
@Path("/digitalInstances")
public class DigitalInstancesResource extends Resource {

    private static final String PARAM_LIBRARY_ID = "libraryId";
    private static final int MAX_URL_LENGTH = 100;
    private final DigitalRepresentation digRep;

    public DigitalInstancesResource() {
        digRep = null;
    }

    public DigitalInstancesResource(DigitalRepresentation digRep) {
        this.digRep = digRep;
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
        if (digRep == null) {
            return new DigitalInstancesBuilder(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilder> instanceBuilders = instanceBuilders(digRep);
            return new DigitalInstancesBuilder(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilder> instanceBuilders(DigitalRepresentation rep) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().instancesByDigRepId(rep.getId());
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
    @Produces("application/xml")
    public Response addNewDigitalInstance(
            @QueryParam(PARAM_LIBRARY_ID) String libraryIdStr, String urlStr) {
        //todo: autentizace
        try {
            long userId = 1;//TODO: zjistit id uzivatele z hlavicky
            if (digRep == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            long libraryId = Parser.parsePositiveLongQueryParam(libraryIdStr, PARAM_LIBRARY_ID);
            URL url = Parser.parseUrlFromRequestBody(urlStr, MAX_URL_LENGTH);
            DigitalInstance instance = newDigitalInstance(libraryId, url);
            instance = dataImportService().addDigitalInstance(instance, userId);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, libraryId);
            String responseXml = builder.buildDocument().toXML();
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownDigiLibException ex) {
            throw new UnknownDigitalLibraryException(ex.getMessage());
        } catch (UnknownDigRepException ex) {
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

    private DigitalInstance newDigitalInstance(long libraryId, URL url) {
        DigitalInstance instance = new DigitalInstance();
        instance.setLibraryId(libraryId);
        instance.setDigRepId(digRep.getId());
        instance.setUrl(url.toString());
        return instance;
    }
}
