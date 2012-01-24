/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.xml.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationResource extends Resource {

    private static final String PARAM_ACTION = "action";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_ADD_DIG_INST = "digitalInstances";
    private final DigitalRepresentation rep;
    private UrnNbn urn;

    public DigitalRepresentationResource(DigitalRepresentation rep, UrnNbn urn) {
        this.rep = rep;
        this.urn = urn;
    }

    private enum Format {

        HTML,
        XML
    }

    private enum Action {

        REDIRECT,
        SHOW,
        DECIDE
    }

    /**
     * Retrieves representation of an instance of cz.nkp.urnnbn.rest.DigitalRepresentationResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml(
            @DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_ADD_DIG_INST) boolean addDigitalInstances) {
        Action action = parseAction(actionStr);
        Format format = parseFormat(formatStr);
        switch (action) {
            case DECIDE://pokud pochazi z katalogu, pouzij redirect s tim,
            //ze pouzijes pouze DR patrici do DL registratora, ktery vlastni ten katalog
            //pokud se nenajde DR, tak se chovej jako pri SHOW
            case REDIRECT://pokus se o redirekt, asi bez preferenci
            //pokud se nenajde nic, tak zobraz dal.
            case SHOW://na zaklade format se rozhodni, jestli zobrazit v gwt nebo xml
        }

        try {
            if (urn == null) {
                urn = dataAccessService().urnByDigRepId(rep.getId());
            }
            //List<DigRepIdentifier> identifiers = dataAccessService().digRepIdentifiersByDigRepId(rep.getId());
            DigitalRepresentationIdentifiersBuilder digRepIdentifiersBuilder = digRepIdentifiersBuilder(rep.getId());
            List<DigitalInstanceBuilder> instancesBuilders = addDigitalInstances
                    ? instancesBuilders(rep) : null;
            RegistrarBuilder regBuilder = new RegistrarBuilder(dataAccessService().registrarById(rep.getRegistrarId()), null, null);
            ArchiverBuilder archBuilder = (rep.getRegistrarId() == rep.getArchiverId())
                    ? null : new ArchiverBuilder(dataAccessService().archiverById(rep.getArchiverId()));
            IntelectualEntityBuilder entityBuilder = entityBuilder(rep.getIntEntId());
            DigitalRepresentationBuilder builder = new DigitalRepresentationBuilder(rep, urn, digRepIdentifiersBuilder, instancesBuilders, regBuilder, archBuilder, entityBuilder);
            return builder.buildRootElement().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private Action parseAction(String actionStr) {
        try {
            return Action.valueOf(actionStr.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(PARAM_ACTION, actionStr, e.getMessage());
        }
    }

    private Format parseFormat(String formatStr) {
        try {
            return Format.valueOf(formatStr.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(PARAM_FORMAT, formatStr, e.getMessage());
        }
    }

    private List<DigitalInstanceBuilder> instancesBuilders(DigitalRepresentation rep) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().instancesByDigRepId(rep.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return result;
    }

    private IntelectualEntityBuilder entityBuilder(long intEntId) throws DatabaseException {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return new IntelectualEntityBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    @Path("/identifiers")
    public DigitalRepresentationIdentifiersResource getIdentifiersResource() {
        return new DigitalRepresentationIdentifiersResource(rep);
    }

    @Path("/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(rep);
    }

    /**
     * PUT method for updating or creating an instance of DigitalRepresentationResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }

    /**
     * DELETE method for resource DigitalRepresentationResource
     */
    @DELETE
    public void delete() {
    }
}
