/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.Action;
import cz.nkp.urnnbn.api.ResponseFormat;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.apiv4.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentResource extends AbstractDigitalDocumentResource {

    private static final Logger logger = Logger.getLogger(cz.nkp.urnnbn.api.v2.DigitalDocumentResource.class.getName());
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_WITH_DIG_INST = "digitalInstances";

    public DigitalDocumentResource(DigitalDocument doc, UrnNbn urn) {
        super(doc, urn);
    }

    @GET
    @Produces("application/xml")
    public Response resolve(@DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr, @Context HttpServletRequest request) {
        try {
            Action action = Parser.parseAction(actionStr, PARAM_ACTION);
            ResponseFormat format = Parser.parseResponseFormat(formatStr, PARAM_FORMAT);
            boolean withDigitalInstances = true;
            if (withDigitalInstancesStr != null) {
                withDigitalInstances = Parser.parseBooleanQueryParam(withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            }
            return resolve(action, format, request, withDigitalInstances);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @Override
    protected Response recordXmlResponse(boolean withDigitalInstances) {
        DigitalDocumentBuilder builder = digitalDocumentBuilder(withDigitalInstances);
        String xml = builder.buildDocumentWithResponseHeader().toXML();
        return Response.ok().entity(xml).build();
    }

    // protected final DigitalDocumentBuilder digitalDocumentBuilder(boolean withDigitalInstances) {
    // return new DigitalDocumentBuilder(doc, urn, apiV4registrarScopeIdentifiersBuilder(doc.getId()), withDigitalInstances ? instancesBuilder() :
    // null,
    // registrarBuilder(), archiverBuilder(), entityBuilder());
    // }

    private DigitalInstancesBuilder instancesBuilder() {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilder(result);
    }

    private RegistrarBuilder registrarBuilder() {
        return new RegistrarBuilder(dataAccessService().registrarById(doc.getRegistrarId()), null, null);
    }

    private ArchiverBuilder archiverBuilder() {
        if (doc.getRegistrarId() == doc.getArchiverId()) {
            return null;
        } else {
            return new ArchiverBuilder(dataAccessService().archiverById(doc.getArchiverId()));
        }
    }

    private IntelectualEntityBuilder entityBuilder() {
        long intEntId = doc.getIntEntId();
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    @Path("/registrarScopeIdentifiers")
    @Override
    public RegistrarScopeIdentifiersResource getRegistrarScopeIdentifiersResource() {
        return new RegistrarScopeIdentifiersResource(doc);
    }

    @Path("/digitalInstances")
    @Override
    public AbstractDigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(doc);
    }
}
