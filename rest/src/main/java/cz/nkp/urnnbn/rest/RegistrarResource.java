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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

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
            @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = true;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = true;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            RegistrarBuilder builder = registrarBuilder(registrar, addDigitalLibraries, addCatalogs);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            //TODO: rid of DatabaseException here
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @Path("digitalDocuments")
    public DigitalDocumentsResource getDigitalDocuments() {
        return new DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
