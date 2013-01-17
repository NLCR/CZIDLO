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
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.UrnNbnDeactivated;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.logging.Level;
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

    private static final String PARAM_ACTION = "action";
    private static final String PARAM_WITH_DIG_INST = "digitalInstances";

    public DigitalDocumentResource(DigitalDocument doc, UrnNbn urn) {
        super(doc, urn);
    }

    @GET
    @Produces("application/xml")
    public Response resolve(
            @DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr,
            @Context HttpServletRequest request) {
        try {
            Action action = Parser.parseAction(actionStr, PARAM_ACTION);
            ResponseFormat format = Parser.parseResponseFormat(formatStr, PARAM_FORMAT);
            boolean withDigitalInstances = true;
            if (withDigitalInstancesStr != null) {
                withDigitalInstances = Parser.parseBooleanQueryParam(withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            }
            loadUrn();
            if (!urn.isActive()) {
                if (action == Action.REDIRECT) {
                    throw new UrnNbnDeactivated(urn);
                } else { //action = SHOW or DECIDE or UNDEFINED
                    action = Action.SHOW;
                }
            }
            return resolve(action, format, request, withDigitalInstances);
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @Path("/registrarScopeIdentifiers")
    public DigitalDocumentIdentifiersResource getIdentifiersResource() {
        return new DigitalDocumentIdentifiersResource(doc);
    }

    @Path("/digitalInstances")
    public AbstractDigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(doc);
    }
}
