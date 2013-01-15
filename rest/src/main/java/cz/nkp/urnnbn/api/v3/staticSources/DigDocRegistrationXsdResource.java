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
package cz.nkp.urnnbn.api.v3.staticSources;

import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author Martin Řehánek
 */
@Path("/digDocRegistration.xsd")
public class DigDocRegistrationXsdResource extends Resource {

    @GET
    @Produces("application/xml")
    public String getDigDocRegistrationXsd() {
        return ApiModuleConfiguration.instanceOf().getDigDocRegistrationXsdV3().toXML();
    }
}
