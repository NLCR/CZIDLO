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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import cz.nkp.urnnbn.api.v4.exceptions.IncorrectUrnStateException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnBuilder;

@Path("/urnnbn")
public class UrnNbnResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnResource.class.getName());
    private static final String PARAM_DEACTIVATION_NOTE = "note";

    @GET
    @Path("{urnNbn}")
    @Produces("text/xml")
    public String getUrnNbnXmlRecord(@PathParam("urnNbn") String urnNbnString) {
        try {
            UrnNbn urnNbn = Parser.parseUrn(urnNbnString);
            UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(),
                    urnNbn.getDocumentCode(), true);
            return new UrnNbnBuilder(urnNbnWithStatus).buildDocumentWithResponseHeader().toXML();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @DELETE
    @Path("{urnNbn}")
    @Produces("text/xml")
    public String deactivateUrnNbn(@Context HttpServletRequest req, @PathParam("urnNbn") String urnNbnString,
            @QueryParam(PARAM_DEACTIVATION_NOTE) String note) {
        try {
            checkServerNotReadOnly();
            UrnNbn urnNbn = Parser.parseUrn(urnNbnString);
            UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(),
                    urnNbn.getDocumentCode(), false);
            switch (urnNbnWithStatus.getStatus()) {
            case ACTIVE:
                String login = req.getRemoteUser();
                dataRemoveService().deactivateUrnNbn(urnNbnWithStatus.getUrn(), login, note);
                UrnNbnWithStatus deactivated = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnWithStatus.getUrn().getRegistrarCode(),
                        urnNbnWithStatus.getUrn().getDocumentCode(), true);
                return new UrnNbnBuilder(deactivated).buildDocumentWithResponseHeader().toXML();
            default:
                throw new IncorrectUrnStateException(urnNbnWithStatus);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (AccessException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

}
