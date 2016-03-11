/*
 * Copyright (C) 2013 Martin Řehánek
 *
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
package cz.nkp.urnnbn.api.v2;

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
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

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
            try {
                String apiV3Response = getUrnNbnReservationsApiV3XmlRecord();
                ApiModuleConfiguration.instanceOf().getGetUrnNbnReservationsResponseV3ToV2Transformer();
                return ApiModuleConfiguration.instanceOf().getGetUrnNbnReservationsResponseV3ToV2Transformer().transform(apiV3Response).toXML();
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new InternalException(e);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }

    @POST
    @Produces("application/xml")
    public Response reserveUrnNbns(@Context HttpServletRequest req, @QueryParam(PARAM_SIZE) String sizeStr) {
        try {
            try {
                checkServerNotReadOnly();
                int size = sizeStr != null ? Parser.parseIntQueryParam(sizeStr, PARAM_SIZE, 1, ApiModuleConfiguration.instanceOf()
                        .getUrnReservationMaxSize()) : ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize();
                String login = req.getRemoteUser();
                String apiV3Response = super.reserveUrnNbns(login, size);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getReserveUrnNbnResponseV3ToV2Transformer();
                String apiV2Response = transformApiV3ToApiV2ResponseAsString(transformer, apiV3Response);
                return Response.created(null).entity(apiV2Response).build();
            } catch (UnknownUserException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (AccessException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }
}
