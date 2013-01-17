/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractRegistrarsResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/registrars")
public class RegistrarsResource extends AbstractRegistrarsResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";
    @Context
    private UriInfo context;

    @GET
    @Produces("application/xml")
    public String getRegistrars(
            @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = false;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = false;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            return super.getRegistrarsXml(addDigitalLibraries, addCatalogs);
        } catch (DatabaseException ex) {
            //TODO: rid of this exception
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    /**
     * Sub-resource locator method for {sigla}
     */
    @Path("{registrarCode}")
    public cz.nkp.urnnbn.api.v3.RegistrarResource getRegistrarResource(@PathParam("registrarCode") String registrarCodeStr) {
        try {
            Registrar registrar = registrarFromRegistarCode(registrarCodeStr);
            return new cz.nkp.urnnbn.api.v3.RegistrarResource(registrar);
        }catch (WebApplicationException ex){
            throw ex;
        }catch (RuntimeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }
}
