/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.api.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.builders.UrnNbnReservationsBuilder;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationsResource extends Resource {

    private static final String PARAM_SIZE = "size";
    private final Registrar registrar;

    public UrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    @Produces("application/xml")
    public String getListOfReservations() {
        try {
            int maxBatchSize = urnReservationService().getMaxBatchSize();
            List<UrnNbn> reservedUrnNbnList = urnReservationService().getReservedUrnNbnList(registrar.getId());
            UrnNbnReservationsBuilder builder = selectBuilder(maxBatchSize, reservedUrnNbnList);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownRegistrarException ex) {
            //should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private UrnNbnReservationsBuilder selectBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
        int maxPrintSize = ApiModuleConfiguration.instanceOf().getMaxReservedSizeToPrint();
        if (reservedUrnNbnList.size() > maxPrintSize) {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(), reservedUrnNbnList.subList(0, maxPrintSize));
        } else {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(), reservedUrnNbnList);
        }
    }

    @POST
    @Produces("application/xml")
    public Response reserveUrnNbns(@Context HttpServletRequest req, @QueryParam(PARAM_SIZE) String sizeStr) {
        int size = ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize();
        if (sizeStr != null) {
            size = Parser.parseIntQueryParam(sizeStr, PARAM_SIZE, 1, ApiModuleConfiguration.instanceOf().getUrnReservationMaxSize());
        }
        if (ApiModuleConfiguration.instanceOf().isServerReadOnly()) {
            throw new MethodForbiddenException();
        } else {
            try {
                String login = req.getRemoteUser();
                List<UrnNbn> reserved = urnReservationService().reserveUrnNbnBatch(size, registrar, login);
                UrnNbnReservationBuilder builder = new UrnNbnReservationBuilder(reserved);
                String responseXml = builder.buildDocumentWithResponseHeader().toXML();
                return Response.created(null).entity(responseXml).build();
            } catch (UnknownUserException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (AccessException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (WebApplicationException e) {
                throw e;
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex.getMessage());
            }
        }
    }
}
