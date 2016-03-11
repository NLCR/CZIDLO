/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.v2_v3.AbstractRegistrarsResource;
import cz.nkp.urnnbn.api.v2_v3.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/registrars")
public class RegistrarsResource extends AbstractRegistrarsResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    @GET
    @Produces("application/xml")
    public String getRegistrars(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = false;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = false;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            return super.getRegistrarsApiV3XmlRecord(addDigitalLibraries, addCatalogs);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    /**
     * Sub-resource locator method for {sigla}
     */
    @Path("{registrarCode}")
    public RegistrarResource getRegistrarResource(@PathParam("registrarCode") String registrarCodeStr) {
        try {
            Registrar registrar = registrarFromRegistarCode(registrarCodeStr);
            return new RegistrarResource(registrar);
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }
}
