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
import cz.nkp.urnnbn.api.DigitalInstanceResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
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
    public String getDigitalInstances() {
        return super.getDigitalInstances();
    }

    @Path("id/{digInstId}")
    public DigitalInstanceResource getDigitalInstanceRestource(@PathParam("digInstId") String digInstIdStr) {
        long id = Parser.parseDigInstId(digInstIdStr);
        return getDetdigitalInstanceResource(id);
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(
            @Context HttpServletRequest req, String content) {
        try {
            if (digDoc == null) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document xmlDocumentV2 = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV2().loadDocument(content);
            Document xmlDocumentV3 = ApiModuleConfiguration.instanceOf().getDigInstImportV2ToV3DataTransformer().transform(xmlDocumentV2);
            DigitalInstance digitalInstance = digitalInstanceFromApiV3Document(xmlDocumentV3);
            return super.addNewDigitalInstance(digitalInstance, login);
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            throw new InternalException(e);
        }
    }
}
