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

import cz.nkp.urnnbn.api.AbstractDigitalDocumentsResource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
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
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentsResource extends AbstractDigitalDocumentsResource {

    public DigitalDocumentsResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalDocumentsXmlRecord() {
        try {
            try {
                String apiV3Response = getDigitalDocumentsApiV3XmlRecord();
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getGetDigDocsResponseV3ToV2Transformer();
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

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String registerDigitalDocument(@Context HttpServletRequest req, String content) {
        try {
            try {
                checkServerNotReadOnly();
                String login = req.getRemoteUser();
                Document apiV2Request = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV2().loadDocument(content);
                Document apiV3Request = ApiModuleConfiguration.instanceOf().getDigDocRegistrationV2ToV3DataTransformer().transform(apiV2Request);
                String apiV3Response = registerDigitalDocumentByApiV3(apiV3Request, login);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getRegisterDigDocResponseV3ToV2Transformer();
                return transformApiV3ToApiV2ResponseAsString(transformer, apiV3Response);
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

    /**
     * Sub-resource locator method for {id}
     */
    @Path("id/{idType}/{idValue}")
    @Override
    public DigitalDocumentResource getDigitalDocumentResource(
            @PathParam("idType") String idTypeStr,
            @PathParam("idValue") String idValue) {
        try {
            try {
                logger.log(Level.INFO, "resolving registrar-scope id (type=''{0}'', value=''{1}'') for registrar {2}", new Object[]{idTypeStr, idValue, registrar.getCode()});
                DigitalDocument digitalDocument = getDigitalDocument(idTypeStr, idValue);
                UrnNbn urn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
                return new DigitalDocumentResource(digitalDocument, urn);
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
