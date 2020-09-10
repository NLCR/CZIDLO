/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import cz.nkp.urnnbn.api.v3.v3_abstract.AbstractRegistrarResource;
import cz.nkp.urnnbn.api.v3.v3_abstract.Parser;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.Registrar;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends AbstractRegistrarResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    public RegistrarResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    public String getRegistrar(@QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr, @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        try {
            boolean addDigitalLibraries = true;
            if (addDigLibsStr != null) {
                addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
            }
            boolean addCatalogs = true;
            if (addCatalogsStr != null) {
                addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
            }
            return super.getRegistrarApiV3XmlRecord(addDigitalLibraries, addCatalogs);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @Path("digitalDocuments")
    @Override
    public DigitalDocumentsResource getDigitalDocuments() {
        return new DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    @Override
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
