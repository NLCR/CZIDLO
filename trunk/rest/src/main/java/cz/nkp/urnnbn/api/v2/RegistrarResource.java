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

import cz.nkp.urnnbn.api.AbstractDigitalDocumentsResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.v3.UrnNbnReservationsResource;
import cz.nkp.urnnbn.core.dto.Registrar;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends cz.nkp.urnnbn.api.AbstractRegistrarResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    public RegistrarResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    public String getRegistrar(
            @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        boolean addDigitalLibraries = true;
        if (addDigLibsStr != null) {
            addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
        }
        boolean addCatalogs = true;
        if (addCatalogsStr != null) {
            addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
        }
        return super.getRegistrarXml(addDigitalLibraries, addCatalogs);
    }

    @Path("digitalDocuments")
    public AbstractDigitalDocumentsResource getDigitalDocuments() {
        return new cz.nkp.urnnbn.api.v2.DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
