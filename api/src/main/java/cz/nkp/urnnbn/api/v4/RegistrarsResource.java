/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4;

import cz.nkp.urnnbn.api.AbstractRegistrarsResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.UnsupportedFormatException;
import cz.nkp.urnnbn.api.v4.jaxb.RegistrarBean;
import cz.nkp.urnnbn.core.dto.Registrar;

import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/registrars")
public class RegistrarsResource extends AbstractRegistrarsResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";
    private static final String PARAM_FORMAT = "format";

    @GET
    @Produces({ "application/xml", "application/json" })
    public Response getRegistrars(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr,
            @QueryParam(PARAM_FORMAT) String format) {
        try {
            boolean addDigitalLibraries = false;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = false;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            // TODO: pokud je null, tak rozhodnout podle hlavicek
            // https://jersey.java.net/nonav/documentation/1.6/user-guide.html#d4e176
            if (format == null || format.equals("xml")) {
                String result = super.getRegistrarsApiV3XmlRecord(addDigitalLibraries, addCatalogs);
                return Response.ok(result, MediaType.APPLICATION_XML).build();
            } else if (format.equals("json")) {
                String result = buildRegistrarsJson(addDigitalLibraries, addCatalogs);
                return Response.ok(result, MediaType.APPLICATION_JSON).build();
                // return
            } else {
                throw new UnsupportedFormatException(format);
            }
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
    @Path("test")
    @GET
    @Produces("application/json")
    public RegistrarBean getRegistrarResource() {
        try {
            Registrar registrar = dataAccessService().registrars().get(0);
            RegistrarBean bean = new RegistrarBean(registrar.getCode().toString(), registrar.getName().toString());
            // return new RegistrarResource(registrar);
            return bean;
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String buildRegistrarsJson(boolean addDigitalLibraries, boolean addCatalogs) {
        List<Registrar> registrars = dataAccessService().registrars();
        return "{\"registrars\": \"TODO\"}";
    }
}
