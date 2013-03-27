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

import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.persistence.AuthorizingProcessDAO;
import cz.nkp.urnnbn.processmanager.persistence.AuthorizingProcessDAOImpl;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/processes")
public class ProcessesResource {

    private static final Logger logger = Logger.getLogger(ProcessResource.class.getName());
    private AuthorizingProcessDAO processDao = AuthorizingProcessDAOImpl.instanceOf();

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public ProcessResource getProcessResource(@PathParam("id") String id) {
        logger.log(Level.INFO, "loading process {0}", id);
        return new ProcessResource(getProcessById(id));
    }

    private Process getProcessById(String idStr) {
        try {
            Long id = Long.valueOf(idStr);
            return processDao.getProcess(id);
        } catch (UnknownRecordException ex) {
            logger.log(Level.INFO, "Unknown process with id{0}", idStr);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            throw new WebApplicationException(response);
        } catch (NumberFormatException e) {
            logger.log(Level.INFO, "Illegal process id {0}", idStr);
            Response response = Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(response);
        }
    }

    private Process testProcess() {
        Process result = new Process();
        result.setId(Long.MIN_VALUE);
        result.setOwnerLogin("martin");
        result.setType(ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
        result.setState(ProcessState.FINISHED);
        result.setScheduled(new Date());
        result.setStarted(new Date());
        result.setFinished(new Date());
        return result;
    }
}
