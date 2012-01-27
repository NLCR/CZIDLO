/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.rest.config.Configuration;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDataException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigInstanceIdException;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import cz.nkp.urnnbn.xml.unmarshallers.DigInstUnmrashaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
@Path("/digitalInstances")
public class DigitalInstancesResource extends Resource {

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
        long id = parseDigInstId(digInstIdStr);
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

    private long parseDigInstId(String digInstIdStr) {
        try {
            return Long.valueOf(digInstIdStr);
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidDigInstanceIdException(digInstIdStr);
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String addNewDigitalInstance(String content) {
        //todo: autentizace
        try {
            if (digRep == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            Document doc = validDocumentFromString(content, Configuration.DIGITAL_INSTANCE_IMPORT_XSD);
            DigInstUnmrashaller unmarshaller = new DigInstUnmrashaller(doc);
            DigitalInstance instance = unmarshaller.getDigitalInstance();
            instance.setDigRepId(digRep.getId());
            //TODO: zjistit id uzivatele z hlavicky
            long userId = 1;
            instance = dataImportService().addDigitalInstance(instance, userId);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            throw new InternalException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (ImportFailedException ex) {
            throw new InternalException(ex.getMessage());
        } catch (RuntimeException e) {
            if (e instanceof WebApplicationException) {
                throw e;
            } else {
                throw new InternalException(e);
            }
        }
    }
}
