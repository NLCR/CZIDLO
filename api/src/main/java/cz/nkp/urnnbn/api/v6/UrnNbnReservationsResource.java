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
package cz.nkp.urnnbn.api.v6;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v6.exceptions.InternalException;
import cz.nkp.urnnbn.api.v6.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v6.json.UrnNbnReservationsBuilderJson;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv6.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.apiv6.builders.UrnNbnReservationsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UrnNbnReservationsResource extends ApiV6Resource {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnReservationsResource.class.getName());

    private static final String PARAM_SIZE = "size";
    private final Registrar registrar;

    public UrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    public Response getUrnNbnReservations(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            int maxBatchSize = urnReservationService().getMaxBatchSize();
            List<UrnNbn> reservedUrnNbnList = urnReservationService().getReservedUrnNbnList(registrar.getId());
            switch (format) {
            case XML:
                String xml = urnNbnReservationsXmlBuilder(maxBatchSize, reservedUrnNbnList).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = urnNbnReservationsJsonBuilder(maxBatchSize, reservedUrnNbnList).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private UrnNbnReservationsBuilderJson urnNbnReservationsJsonBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
        int maxPrintSize = ApiModuleConfiguration.instanceOf().getMaxReservedSizeToPrint();
        if (reservedUrnNbnList.size() > maxPrintSize) {
            return new UrnNbnReservationsBuilderJson(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList.subList(0, maxPrintSize));
        } else {
            return new UrnNbnReservationsBuilderJson(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList);
        }
    }

    private UrnNbnReservationsBuilder urnNbnReservationsXmlBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
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
        // TODO:APIv6: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            int size = sizeStr != null ? Parser.parseIntQueryParam(format, sizeStr, PARAM_SIZE, 1, ApiModuleConfiguration.instanceOf()
                    .getUrnReservationMaxSize()) : ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize();
            String login = req.getRemoteUser();
            String responseXml = reserveUrnNbnsWithXmlResponse(login, size);
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private final String reserveUrnNbnsWithXmlResponse(String login, int size) throws UnknownUserException, AccessException {
        List<UrnNbn> reserved = urnReservationService().reserveUrnNbnBatch(size, registrar, login);
        UrnNbnReservationBuilder builder = new UrnNbnReservationBuilder(reserved);
        return builder.buildDocumentWithResponseHeader().toXML();
    }
}
