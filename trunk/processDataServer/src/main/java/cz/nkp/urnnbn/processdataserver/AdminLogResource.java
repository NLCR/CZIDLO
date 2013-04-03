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
package cz.nkp.urnnbn.processdataserver;

import cz.nkp.urnnbn.processdataserver.conf.ProcessDataServerConfiguration;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("adminLog")
public class AdminLogResource {

    private static String ADMIN_LOG_FILENAME = "admin_log.txt";

    @GET
    @Produces("text/plain")
    public Response getLogFile() {
        File file = new File(ProcessDataServerConfiguration.instanceOf().getAdminLogFile());
        Response.ResponseBuilder builder = Response.ok(file);
        builder.header("Content-Disposition", "attachment; filename=\"" + ADMIN_LOG_FILENAME + "\"");
        return builder.build();
    }
}
