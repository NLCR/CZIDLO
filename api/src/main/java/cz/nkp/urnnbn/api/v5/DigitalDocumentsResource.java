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
package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.v5.exceptions.InternalException;
import cz.nkp.urnnbn.api.v5.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/digitalDocuments")
public class DigitalDocumentsResource extends AbstractDigitalDocumentResource {

    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentsResource.class.getName());

    @GET
    @Path("id/{digDocId}")
    public Response getDigitalDocumentResourceByInternalId(@Context HttpServletRequest context,
                                                           @PathParam("digDocId") String digDocIdStr,
                                                           @DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
                                                           @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        long digDocId = Parser.parseDigDocId(format, digDocIdStr);
        boolean withDigitalInstances = Parser.parseBooleanQueryParam(format, withDigitalInstancesStr, PARAM_WITH_DIG_INST);
        try {
            DigitalDocument doc = dataAccessService().digDocByInternalId(digDocId);
            if (doc == null) {
                throw new UnknownDigitalDocumentException(format, digDocId);
            } else {
                UrnNbn urn = dataAccessService().urnByDigDocId(digDocId, true);
                return metadataResponse(doc, urn, format, withDigitalInstances);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private Response metadataResponse(DigitalDocument doc, UrnNbn urnNbn, ResponseFormat format, boolean withDigitalInstances) {
        switch (format) {
            case XML:
                String xml = digitalDocumentBuilderXml(doc, urnNbn, withDigitalInstances).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = digitalDocumentBuilderJson(doc, urnNbn, withDigitalInstances).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            default:
                throw new RuntimeException();
        }
    }

}
