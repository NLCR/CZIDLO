/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractDigitalDocumentResource;
import cz.nkp.urnnbn.api.AbstractDigitalInstancesResource;
import cz.nkp.urnnbn.api.Action;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.ResponseFormat;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
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
