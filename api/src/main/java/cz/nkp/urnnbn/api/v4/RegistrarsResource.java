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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
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

    @GET
    @Produces("application/xml")
    public String getRegistrars(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = false;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = false;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            return getRegistrarsApiV4XmlRecord(addDigitalLibraries, addCatalogs);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String getRegistrarsApiV4XmlRecord(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
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

    @Path("{registrarCode}")
    public RegistrarResource getRegistrarResource(@PathParam("registrarCode") String registrarCodeStr) {
        try {
            Registrar registrar = registrarFromRegistarCode(registrarCodeStr);
            return new RegistrarResource(registrar);
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private Registrar registrarFromRegistarCode(String registrarCodeStr) {
        RegistrarCode registrarCode = Parser.parseRegistrarCode(registrarCodeStr);
        Registrar registrar = dataAccessService().registrarByCode(registrarCode);
        if (registrar == null) {
            throw new UnknownRegistrarException(registrarCode);
        } else {
            return registrar;
        }
    }
}
