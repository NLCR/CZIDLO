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
package cz.nkp.urnnbn.api.v6;

import cz.nkp.urnnbn.api.v6.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarResource extends ApiV6Resource {

    private static final Logger LOGGER = Logger.getLogger(RegistrarResource.class.getName());

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    private final Registrar registrar;

    public RegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @Path("digitalDocuments")
    public DigitalDocumentsOfRegistrarResource getDigitalDocuments() {
        return new DigitalDocumentsOfRegistrarResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }

    @GET
    public Response getRegistrar(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @DefaultValue("true") @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        boolean addDigitalLibraries = Parser.parseBooleanQueryParam(format, addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
        boolean addCatalogs = Parser.parseBooleanQueryParam(format, addCatalogsStr, PARAM_CATALOGS);
        try {
            switch (format) {
            case XML: {
                String xml = registrarBuilderXml(registrar, addDigitalLibraries, addCatalogs).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                String json = registrarBuilderJson(registrar, addDigitalLibraries, addCatalogs).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            }
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

}
