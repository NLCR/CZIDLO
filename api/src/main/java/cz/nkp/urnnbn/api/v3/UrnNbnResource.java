/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractUrnNbnResource;
import cz.nkp.urnnbn.api.v3.exceptions.IncorrectUrnStateException;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

/**
 * 
 * @author Martin Řehánek
 */
@Path("/urnnbn")
public class UrnNbnResource extends AbstractUrnNbnResource {

	@GET
	@Path("{urn}")
	@Produces("text/xml")
	@Override
	public String getUrnNbnXmlRecord(@PathParam("urn") String urnNbnString) {
		try {
			UrnNbnWithStatus urnNbnWithStatus = getUrnNbnWithStatus(urnNbnString);
			return new UrnNbnBuilder(urnNbnWithStatus).buildDocumentWithResponseHeader().toXML();
		} catch (WebApplicationException e) {
			throw e;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new InternalException(e);
		}
	}

	@DELETE
	@Path("{urn}")
	@Produces("text/xml")
	public String deactivateUrnNbn(@Context HttpServletRequest req, @PathParam("urn") String urnNbnString, @QueryParam("note") String note) {
		try {
			checkServerNotReadOnly();
			UrnNbnWithStatus urnNbnWithStatus = getUrnNbnWithStatus(urnNbnString);
			switch (urnNbnWithStatus.getStatus()) {
			case ACTIVE:
				String login = req.getRemoteUser();
				dataRemoveService().deactivateUrnNbn(urnNbnWithStatus.getUrn(), login, note);
				UrnNbnWithStatus deactivated = getUrnNbnWithStatus(urnNbnWithStatus.getUrn());
				return new UrnNbnBuilder(deactivated).buildDocumentWithResponseHeader().toXML();
			default:
				throw new IncorrectUrnStateException(urnNbnWithStatus);
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (AccessException e) {
			throw new NotAuthorizedException(e.getMessage());
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new InternalException(e);
		}
	}
}
