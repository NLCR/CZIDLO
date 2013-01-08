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

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v3.DigitalDocumentResource;
import cz.nkp.urnnbn.core.dto.Registrar;
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
public class DigitalDocumentsResource extends cz.nkp.urnnbn.api.AbstractDigitalDocumentsResource {

    public DigitalDocumentsResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalDocuments() {
        return super.getDigitalDocuments();
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String registerDigitalDocument(@Context HttpServletRequest req, String content) {
        try {
            String login = req.getRemoteUser();
            //TODO: validace podle schema pro v2
            System.err.println("BEFORE V2 VALIDATION");
            //Document v2Doc = validDocumentFromString(content, ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV2());
            Document v2Doc = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV2().loadDocument(content);

            System.err.println("V2 VALIDATION OK");
            Document transformed = ApiModuleConfiguration.instanceOf().getDigDocRegistrationV2ToV3DataTransformer().transform(v2Doc);
            System.err.println("V2-V3 TRANSFORMATION OK");

            //TODO: transformace do formy pro v3
            //String transformed = content;
            return registerDigitalDocumentByApiV3(transformed, login);
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
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
        return super.getDigitalDocumentResource(idTypeStr, idValue);
    }
}
