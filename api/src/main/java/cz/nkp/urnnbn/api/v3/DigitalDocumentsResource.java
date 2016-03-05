/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;

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
import javax.ws.rs.core.Response;
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
    public String getDigitalDocumentsXmlRecord() {
        try {
            return super.getDigitalDocumentsApiV3XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response registerDigitalDocument(@Context HttpServletRequest req, String content) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            String response = registerDigitalDocumentByApiV3(content, login, registrar.getCode());
            return Response.created(null).entity(response).build();
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("registrarScopeIdentifier/{idType}/{idValue}")
    @Override
    public DigitalDocumentResource getDigitalDocumentResource(@PathParam("idType") String idTypeStr, @PathParam("idValue") String idValueStr) {
        try {
            logger.log(Level.INFO, "resolving registrar-scope id (type=''{0}'', value=''{1}'') for registrar {2}", new Object[] { idTypeStr,
                    idValueStr, registrar.getCode() });
            DigitalDocument digitalDocument = super.getDigitalDocument(idTypeStr, idValueStr);
            UrnNbn urn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            return new DigitalDocumentResource(digitalDocument, urn);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
