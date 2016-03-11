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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.xml.apiv3.builders.ErrorResponseBuilder;

/**
 *
 * @author Martin Řehánek
 */
public class ApiV2Exception extends WebApplicationException {

    /**
     * Creates a new instance of <code>ApiV2Exception</code> without detail message.
     */
    public ApiV2Exception(ApiV3Exception e) {
        super(buildResponse(e.getStatus(), e.getErrorCode(), e.getErrorMessage()));
    }

    private static Response buildResponse(Response.Status status, String errorCode, String errorMessage) {
        Response.ResponseBuilder builder = Response.status(status);
        String apiV3Response = buildEntityXml(errorCode, errorMessage);
        builder.entity(transformToV2Response(apiV3Response));
        builder.type("text/xml");
        return builder.build();
    }

    private static String buildEntityXml(String errorCode, String errorMessage) {
        ErrorResponseBuilder builder = new ErrorResponseBuilder(errorCode, errorMessage);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private static String transformToV2Response(String apiV3Response) {
        try {
            return ApiModuleConfiguration.instanceOf().getErrorResponseV3ToV2Transformer().transform(apiV3Response).toXML();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
