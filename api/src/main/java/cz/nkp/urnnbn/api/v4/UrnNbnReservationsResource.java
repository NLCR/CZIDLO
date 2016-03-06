/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v4;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnReservationsBuilder;

public class UrnNbnReservationsResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnReservationsResource.class.getName());

    private static final String PARAM_SIZE = "size";
    private final Registrar registrar;

    public UrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    @Produces("application/xml")
    public String getUrnNbnReservationsXmlRecord() {
        try {
            return buildUrnNbnReservationsXmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private final String buildUrnNbnReservationsXmlRecord() throws UnknownRegistrarException {
        int maxBatchSize = urnReservationService().getMaxBatchSize();
        List<UrnNbn> reservedUrnNbnList = urnReservationService().getReservedUrnNbnList(registrar.getId());
        UrnNbnReservationsBuilder builder = selectBuilder(maxBatchSize, reservedUrnNbnList);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private UrnNbnReservationsBuilder selectBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
        int maxPrintSize = ApiModuleConfiguration.instanceOf().getMaxReservedSizeToPrint();
        if (reservedUrnNbnList.size() > maxPrintSize) {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList.subList(0, maxPrintSize));
        } else {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList);
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
            String responseXml = reserveUrnNbnsWithXmlResponse(login, size);
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private final String reserveUrnNbnsWithXmlResponse(String login, int size) throws UnknownUserException, AccessException {
        List<UrnNbn> reserved = urnReservationService().reserveUrnNbnBatch(size, registrar, login);
        UrnNbnReservationBuilder builder = new UrnNbnReservationBuilder(reserved);
        return builder.buildDocumentWithResponseHeader().toXML();
    }
}
