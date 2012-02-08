/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/registrars")
public class RegistrarsResource extends Resource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";
    @Context
    private UriInfo context;

    @GET
    @Produces("application/xml")
    public String getRegistrars(
            @DefaultValue("false") @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @DefaultValue("false") @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = queryParamToBoolean(addDigLibsStr, PARAM_DIGITAL_LIBRARIES, false);
            boolean addCatalogs = queryParamToBoolean(addCatalogsStr, PARAM_CATALOGS, false);
            List<RegistrarBuilder> registrarBuilders = registrarBuilderList(addDigitalLibraries, addCatalogs);
            RegistrarsBuilder builder = new RegistrarsBuilder(registrarBuilders);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    /**
     * Sub-resource locator method for {sigla}
     */
    @Path("{sigla}")
    public RegistrarResource getRegistrarResource(@PathParam("sigla") String siglaStr) {
        try {
            RegistrarCode sigla = Parser.parseSigla(siglaStr);
            Registrar registrar = dataAccessService().registrarByCode(sigla);
            if (registrar == null) {
                throw new UnknownRegistrarException(sigla);
            }
            return new RegistrarResource(registrar);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private List<RegistrarBuilder> registrarBuilderList(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilder> result = new ArrayList<RegistrarBuilder>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(registrarBuilder(registrar, addDigitalLibraries, addCatalogs));
        }
        return result;
    }
}
