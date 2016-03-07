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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.exceptions.IllegalFormatError;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.JsonVersionNotImplementedError;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarsBuilder;

@Path("/registrars")
public class RegistrarsResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(RegistrarsResource.class.getName());

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    @Path("{registrarCode}")
    public RegistrarResource getRegistrarResource(@PathParam("registrarCode") String registrarCodeStr) {
        ResponseFormat format = ResponseFormat.XML;// TODO: parse format, support xml and json
        try {
            Registrar registrar = registrarFromRegistarCode(format, registrarCodeStr);
            return new RegistrarResource(registrar);
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private Registrar registrarFromRegistarCode(ResponseFormat format, String registrarCodeStr) {
        RegistrarCode registrarCode = Parser.parseRegistrarCode(format, registrarCodeStr);
        Registrar registrar = dataAccessService().registrarByCode(registrarCode);
        if (registrar == null) {
            throw new UnknownRegistrarException(format, registrarCode);
        } else {
            return registrar;
        }
    }

    @GET
    public Response getRegistrars(@QueryParam(PARAM_FORMAT) String formatStr, @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        ResponseFormat format = Parser.parseFormatXmlIfNullOrEmpty(formatStr);
        if (format == ResponseFormat.JSON) { // TODO: remove when implemented
            throw new JsonVersionNotImplementedError(format);
        }
        boolean addDigitalLibraries = Parser.parseBooleanQueryParamDefaultIfNullOrEmpty(format, addDigLibsStr, PARAM_DIGITAL_LIBRARIES, false);
        boolean addCatalogs = Parser.parseBooleanQueryParamDefaultIfNullOrEmpty(format, addCatalogsStr, PARAM_CATALOGS, false);
        try {
            switch (format) {
            case XML: {
                String xml = buildRegistrarsXmlRecord(addDigitalLibraries, addCatalogs);
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                // TODO: implement json version
                throw new JsonVersionNotImplementedError(format);
            }
            default:
                throw new IllegalFormatError(ResponseFormat.XML, formatStr);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private String buildRegistrarsXmlRecord(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<RegistrarBuilder> registrarBuilders = registrarBuilderList(addDigitalLibraries, addCatalogs);
        RegistrarsBuilder builder = new RegistrarsBuilder(registrarBuilders);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private List<RegistrarBuilder> registrarBuilderList(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilder> result = new ArrayList<RegistrarBuilder>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(registrarBuilder(registrar, addDigitalLibraries, addCatalogs));
        }
        return result;
    }

}
