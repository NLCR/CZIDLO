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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;

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
    @Produces("application/xml")
    public String getRegistrar(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        ResponseFormat format = ResponseFormat.XML;// TODO: parse format, support xml and json
        try {
            boolean addDigitalLibraries = true;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(format, addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = true;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(format, addCatalogsStr, PARAM_CATALOGS);
            }
            return getRegistrarRecordXml(addDigitalLibraries, addCatalogs);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private String getRegistrarRecordXml(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        RegistrarBuilder builder = registrarBuilder(registrar, addDigitalLibraries, addCatalogs);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

}
