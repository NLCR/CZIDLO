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

import cz.nkp.urnnbn.api.AbstractDigitalInstancesResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
@Path("/digitalInstances")
public class DigitalInstancesResource extends AbstractDigitalInstancesResource {

    public DigitalInstancesResource() {
        super(null);
    }

    public DigitalInstancesResource(DigitalDocument digDoc) {
        super(digDoc);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalInstancesXmlRecord() {
        try {
            try {
                String apiV3Response = super.getDigitalInstancesApiV3XmlRecord();
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getGetDigInstsResponseV3ToV2Transformer();
                return transformApiV3ToApiV2ResponseAsString(transformer, apiV3Response);
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

    @Path("id/{digInstId}")
    @Override
    public DigitalInstanceResource getDigitalInstanceResource(@PathParam("digInstId") String digInstIdStr) {
        try {
            try {
                long id = Parser.parseDigInstId(digInstIdStr);
                DigitalInstance digitalInstance = getDigitalInstance(id);
                return new DigitalInstanceResource(digitalInstance);
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
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(@Context HttpServletRequest req, String content) {
        try {
            try {
                checkServerNotReadOnly();
                if (digDoc == null) {
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                String login = req.getRemoteUser();
                Document apiV2Request = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV2().loadDocument(content);
                Document apiV3Request = ApiModuleConfiguration.instanceOf().getDigInstImportV2ToV3DataTransformer().transform(apiV2Request);
                DigitalInstance digitalInstance = digitalInstanceFromApiV3Document(apiV3Request);
                String apiV3Response = super.addNewDigitalInstanceWithApiV3Response(digitalInstance, login);
                XsltXmlTransformer responseTransformer = ApiModuleConfiguration.instanceOf().getImportDigitalInstanceResponseV3ToV2Transformer();
                String apiV2Response = transformApiV3ToApiV2ResponseAsString(responseTransformer, apiV3Response);
                return Response.created(null).entity(apiV2Response).build();
            } catch (ValidityException ex) {
                throw new InvalidDataException(ex);
            } catch (ParsingException ex) {
                throw new InvalidDataException(ex);
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
}
