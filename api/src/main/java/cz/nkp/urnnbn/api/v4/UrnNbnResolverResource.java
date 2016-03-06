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

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownUrnException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;

@Path("/resolver")
public class UrnNbnResolverResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnResolverResource.class.getName());

    @Path("{urn}")
    public Resource getDigitalDocumentResource(@PathParam("urn") String urnPar) {
        ResponseFormat format = ResponseFormat.XML;// TODO: parse format, support xml and json
        try {
            UrnNbn urnParsed = Parser.parseUrn(format, urnPar);
            UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(),
                    urnParsed.getDocumentCode(), true);
            switch (fetched.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                DigitalDocument doc = dataAccessService().digDocByInternalId(fetched.getUrn().getDigDocId());
                if (doc == null) {
                    throw new UnknownDigitalDocumentException(format, fetched.getUrn());
                } else {
                    // update resolvations statistics
                    statisticService().incrementResolvationStatistics(urnParsed.getRegistrarCode().toString());
                    return new DigitalDocumentResource(doc, fetched.getUrn());
                }
            case FREE:
                throw new UnknownUrnException(format, urnParsed);
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, fetched.getUrn());
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e.getMessage());
        }
    }
}
