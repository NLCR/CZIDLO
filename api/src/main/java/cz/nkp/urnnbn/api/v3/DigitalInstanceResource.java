/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import cz.nkp.urnnbn.api.v3.v3_abstract.AbstractDigitalInstanceResource;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.DigitalInstance;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceResource extends AbstractDigitalInstanceResource {

    public DigitalInstanceResource(DigitalInstance instance) {
        super(instance);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalInstanceXmlRecord() {
        try {
            return getDigitalInstanceApiV3XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return super.deactivateDigitalInstance(login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
