/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.rest.config.Configuration;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.xml.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.builders.UrnNbnReservationsBuilder;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationsResource extends Resource {

    private static final String PARAM_SIZE = "size";
    private static final int DEFAULT_BATCH_SIZE = Configuration.URN_RESERVATION_DEFAULT_SIZE;
    private static final int MAX_BATCH_SIZE = Configuration.URN_RESERVATION_MAX_SIZE;
    private final Registrar registrar;

    public UrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    @Produces("application/xml")
    public String getReservations() {
        try {
            int maxBatchSize = urnReservationService().getMaxBatchSize();
            List<UrnNbn> reservedUrnNbnList = urnReservationService().getReservedUrnNbnList(registrar.getId());
            UrnNbnReservationsBuilder builder = selectBuilder(maxBatchSize, reservedUrnNbnList);
            return builder.buildDocument().toXML();
        } catch (UnknownRegistrarException ex) {
            //should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private UrnNbnReservationsBuilder selectBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
        if (reservedUrnNbnList.size() > Configuration.MAX_RESERVED_SIZE_TO_PRINT) {
            return new UrnNbnReservationsBuilder(maxBatchSize, DEFAULT_BATCH_SIZE, reservedUrnNbnList.size());
        } else {
            return new UrnNbnReservationsBuilder(maxBatchSize, DEFAULT_BATCH_SIZE, reservedUrnNbnList);
        }
    }

    @POST
    @Produces("application/xml")
    public Response createReservation(@QueryParam(PARAM_SIZE) String sizeStr) {
        int size = sizeStr == null
                ? DEFAULT_BATCH_SIZE : Parser.parseIntQueryParam(sizeStr, PARAM_SIZE, 1, MAX_BATCH_SIZE);
        if (Configuration.SERVER_READ_ONLY) {
            throw new MethodForbiddenException();
        } else {
            try {
                int userId = 1;//TODO: zjistit z hlavicky
                List<UrnNbn> reserved = urnReservationService().reserveUrnNbnBatch(size, registrar, userId);
                UrnNbnReservationBuilder builder = new UrnNbnReservationBuilder(reserved);
                String responseXml = builder.buildDocument().toXML();
                return Response.created(null).entity(responseXml).build();
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex.getMessage());
            }
        }
    }
}
