/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.Registrar;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentsResource extends cz.nkp.urnnbn.api.AbstractDigitalDocumentsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RegistrarsResource
     */
    public DigitalDocumentsResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalDocuments() {
        return super.getDigitalDocuments();
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String registerDigitalDocument(@Context HttpServletRequest req, String content) {
        try {
            String login = req.getRemoteUser();
            return registerDigitalDocumentByApiV3(content, login);
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("id/{idType}/{idValue}")
    @Override
    public DigitalDocumentResource getDigitalDocumentResource(
            @PathParam("idType") String idTypeStr,
            @PathParam("idValue") String idValue) {
        return super.getDigitalDocumentResource(idTypeStr, idValue);
    }
}
