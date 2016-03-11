/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v2_v3.AbstractUrnNbnReservationsResource;
import cz.nkp.urnnbn.api.v2_v3.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationsResource extends AbstractUrnNbnReservationsResource {

    private static final String PARAM_SIZE = "size";

    public UrnNbnReservationsResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getUrnNbnReservationsXmlRecord() {
        try {
            return getUrnNbnReservationsApiV3XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @POST
    @Produces("application/xml")
    public Response reserveUrnNbns(@Context HttpServletRequest req, @QueryParam(PARAM_SIZE) String sizeStr) {
        try {
            checkServerNotReadOnly();
            int size = sizeStr != null ? Parser.parseIntQueryParam(sizeStr, PARAM_SIZE, 1, ApiModuleConfiguration.instanceOf()
                    .getUrnReservationMaxSize()) : ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize();
            String login = req.getRemoteUser();
            String responseXml = super.reserveUrnNbns(login, size);
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
