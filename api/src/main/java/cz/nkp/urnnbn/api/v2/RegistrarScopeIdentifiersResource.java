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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v2_v3.AbstractRegistrarScopeIdentifiersResource;
import cz.nkp.urnnbn.api.v2_v3.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifiersResource extends AbstractRegistrarScopeIdentifiersResource {

    public RegistrarScopeIdentifiersResource(DigitalDocument doc) {
        super(doc);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getRegistrarScopeIdentifiersXmlRecord() {
        try {
            try {
                String apiV3Response = super.getRegistrarScopeIdentifiersApiV3XmlRecord();
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getGetRegScopeIdsResponseV3ToV2Transformer();
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

    @GET
    @Path("/{idType}")
    @Produces("application/xml")
    @Override
    public String getRegistrarScopeIdentifierValue(@PathParam("idType") String idTypeStr) {
        try {
            try {
                RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
                String apiV3Response = getRegistrarScopeIdentifierValueApiV3XmlRecord(idType);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getGetRegScopeIdResponseV3ToV2Transformer();
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

    @PUT
    @Path("/{idType}")
    @Produces("application/xml")
    public Response setOrUpdateIdentifierValue(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr, String idValueStr) {
        try {
            try {
                checkServerNotReadOnly();
                String login = req.getRemoteUser();
                RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
                RegistrarScopeIdValue idValue = Parser.parseRegistrarScopeIdValue(idValueStr);
                RegistrarScopeIdentifier oldId = presentIdentifierOrNull(idType);
                if (oldId == null) { // insert new value
                    RegistrarScopeIdentifier newId = addNewIdentifier(idType, idValue, login);
                    String apiV3Response = new RegistrarScopeIdentifierBuilder(newId).buildDocumentWithResponseHeader().toXML();
                    XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getSetOrUpdateRegScopeIdResponseV3ToV2Transformer();
                    String transformedResponse = transformApiV3ToApiV2ResponseAsString(transformer, apiV3Response);
                    return Response.created(null).entity(transformedResponse).build();
                } else { // update value
                    RegistrarScopeIdentifier newId = updateIdentifier(login, idType, idValue);
                    String apiV3Response = new RegistrarScopeIdentifierBuilder(newId, oldId.getValue()).buildDocumentWithResponseHeader().toXML();
                    XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getSetOrUpdateRegScopeIdResponseV3ToV2Transformer();
                    String transformedResponse = transformApiV3ToApiV2ResponseAsString(transformer, apiV3Response);
                    return Response.ok().entity(transformedResponse).build();
                }
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

    @DELETE
    @Path("/{idType}")
    @Produces("application/xml")
    public String deleteRegistrarScopeIdentifier(@Context HttpServletRequest req, @PathParam("idType") String idTypeStr) {
        try {
            try {
                checkServerNotReadOnly();
                String login = req.getRemoteUser();
                String apiV3Response = super.deleteRegistrarScopeIdentifierWithApiV3Response(login, idTypeStr);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getDeleteRegScopeIdResponseV3ToV2Transformer();
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

    @DELETE
    @Produces("application/xml")
    public String removeAllIdentifiers(@Context HttpServletRequest req) {
        try {
            try {
                checkServerNotReadOnly();
                String login = req.getRemoteUser();
                String apiV3Response = super.deleteAllRegistrarScopeIdentifiersWithApiV3Response(login);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getDeleteRegScopeIdsResponseV3ToV2Transformer();
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
}
