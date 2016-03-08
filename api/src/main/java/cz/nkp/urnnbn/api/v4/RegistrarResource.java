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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.exceptions.IllegalFormatException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.JsonVersionNotImplementedException;
import cz.nkp.urnnbn.core.dto.Registrar;

public class RegistrarResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(RegistrarResource.class.getName());

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    private final Registrar registrar;

    public RegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @Path("digitalDocuments")
    public DigitalDocumentsResource getDigitalDocuments() {
        return new DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }

    @GET
    public Response getRegistrar(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        Format format = Parser.parseFormat(formatStr);
        if (format == Format.JSON) { // TODO: remove when implemented
            throw new JsonVersionNotImplementedException(format);
        }
        boolean addDigitalLibraries = Parser.parseBooleanQueryParamDefaultIfNullOrEmpty(format, addDigLibsStr, PARAM_DIGITAL_LIBRARIES, true);
        boolean addCatalogs = Parser.parseBooleanQueryParamDefaultIfNullOrEmpty(format, addCatalogsStr, PARAM_CATALOGS, true);
        try {
            switch (format) {
            case XML: {
                String xml = registrarBuilder(registrar, addDigitalLibraries, addCatalogs).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                // TODO: implement json version
                throw new JsonVersionNotImplementedException(format);
            }
            default:
                throw new IllegalFormatException(Format.XML, formatStr);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

}
