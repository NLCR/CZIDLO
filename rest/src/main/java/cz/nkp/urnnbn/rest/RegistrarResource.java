/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends Resource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";
    private Registrar registrar;

    public RegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    @Produces("application/xml")
    public String getRegistrar(
            @DefaultValue("true") @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @DefaultValue("true") @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = queryParamToBoolean(addDigLibsStr, PARAM_DIGITAL_LIBRARIES, true);
            boolean addCatalogs = queryParamToBoolean(addCatalogsStr, PARAM_CATALOGS, true);
            RegistrarBuilder builder = registrarBuilder(registrar, addDigitalLibraries, addCatalogs);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @Path("digitalRepresentations")
    public DigitalRepresentationsResource getDigitalRepresentations() {
        return new DigitalRepresentationsResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
