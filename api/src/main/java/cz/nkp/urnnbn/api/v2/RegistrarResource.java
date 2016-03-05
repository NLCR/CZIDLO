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

import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.AbstractRegistrarResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends AbstractRegistrarResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    public RegistrarResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    public String getRegistrarXmlRecord(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            try {
                boolean addDigitalLibraries = true;
                if (addDigLibsStr != null) {
                    addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
                }
                boolean addCatalogs = true;
                if (addCatalogsStr != null) {
                    addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
                }
                String apiV3Response = super.getRegistrarApiV3XmlRecord(addDigitalLibraries, addCatalogs);
                return ApiModuleConfiguration.instanceOf().getGetRegistrarResponseV3ToV2Transformer().transform(apiV3Response).toXML();
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new InternalException(e);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }

    @Path("digitalDocuments")
    @Override
    public DigitalDocumentsResource getDigitalDocuments() {
        return new DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    @Override
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
