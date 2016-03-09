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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.api.v4.json.RegistrarBuilderJson;
import cz.nkp.urnnbn.api.v4.json.RegistrarsBuilderJson;
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
    public RegistrarResource getRegistrarResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("registrarCode") String registrarCodeStr) {
        Format format = Parser.parseFormat(formatStr);
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

    private Registrar registrarFromRegistarCode(Format format, String registrarCodeStr) {
        RegistrarCode registrarCode = Parser.parseRegistrarCode(format, registrarCodeStr);
        Registrar registrar = dataAccessService().registrarByCode(registrarCode);
        if (registrar == null) {
            throw new UnknownRegistrarException(format, registrarCode);
        } else {
            return registrar;
        }
    }

    @GET
    public Response getRegistrars(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("false") @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @DefaultValue("false") @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        Format format = Parser.parseFormat(formatStr);
        boolean addDigitalLibraries = Parser.parseBooleanQueryParam(format, addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
        boolean addCatalogs = Parser.parseBooleanQueryParam(format, addCatalogsStr, PARAM_CATALOGS);
        try {
            switch (format) {
            case XML:
                String xml = registrarBuilderXml(addDigitalLibraries, addCatalogs).buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = registrarBuilderJson(addDigitalLibraries, addCatalogs).toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
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

    private RegistrarsBuilder registrarBuilderXml(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilder> registrarBuilders = new ArrayList<RegistrarBuilder>(registrars.size());
        for (Registrar registrar : registrars) {
            registrarBuilders.add(registrarBuilderXml(registrar, addDigitalLibraries, addCatalogs));
        }
        return new RegistrarsBuilder(registrarBuilders);
    }

    private RegistrarsBuilderJson registrarBuilderJson(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilderJson> registrarBuilders = new ArrayList<RegistrarBuilderJson>(registrars.size());
        for (Registrar registrar : registrars) {
            registrarBuilders.add(registrarBuilderJson(registrar, addDigitalLibraries, addCatalogs));
        }
        return new RegistrarsBuilderJson(registrarBuilders);
    }

}
