/*
 * Copyright (C) 2017 Martin Řehánek
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

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
import cz.nkp.urnnbn.processmanager.persistence.*;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/transformations")
public class TransformationsResource {

    private static final Logger logger = Logger.getLogger(ProcessResource.class.getName());
    private AuthorizingProcessDAO processDao = AuthorizingProcessDAOImpl.instanceOf();
    private XmlTransformationDAO xmlTransformationDao = XmlTransformationDAOImpl.instanceOf();

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public TransformationResource getProcessResource(@PathParam("id") String id) {
        logger.log(Level.FINE, "loading transformation {0}", id);
        return new TransformationResource(getTransformationById(id));
    }

    private XmlTransformation getTransformationById(String idStr) {
        try {
            Long id = Long.valueOf(idStr);
            return xmlTransformationDao.getTransformation(id);
        } catch (UnknownRecordException ex) {
            logger.log(Level.INFO, "Unknown transformation {0}", idStr);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            throw new WebApplicationException(response);
        } catch (NumberFormatException e) {
            logger.log(Level.INFO, "Illegal transformation id {0}", idStr);
            Response response = Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(response);
        }
    }

}
