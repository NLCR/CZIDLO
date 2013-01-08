/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3;

import cz.nkp.urnnbn.api.AbstractDigitalDocumentsResource;
import cz.nkp.urnnbn.api.Parser;
import cz.nkp.urnnbn.core.dto.Registrar;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends cz.nkp.urnnbn.api.AbstractRegistrarResource {

    private static final String PARAM_DIGITAL_LIBRARIES = "digitalLibraries";
    private static final String PARAM_CATALOGS = "catalogs";

    public RegistrarResource(Registrar registrar) {
        super(registrar);
    }

    @GET
    @Produces("application/xml")
    public String getRegistrar(
            @QueryParam(PARAM_DIGITAL_LIBRARIES) String addDigLibsStr,
            @QueryParam(PARAM_CATALOGS) String addCatalogsStr) {
        boolean addDigitalLibraries = true;
        if (addDigLibsStr != null) {
            addDigitalLibraries = Parser.parseBooleanQueryParam(addDigLibsStr, PARAM_DIGITAL_LIBRARIES);
        }
        boolean addCatalogs = true;
        if (addCatalogsStr != null) {
            addCatalogs = Parser.parseBooleanQueryParam(addCatalogsStr, PARAM_CATALOGS);
        }
        return super.getRegistrarXml(addDigitalLibraries, addCatalogs);
    }

    @Path("digitalDocuments")
    public AbstractDigitalDocumentsResource getDigitalDocuments() {
        return new cz.nkp.urnnbn.api.v3.DigitalDocumentsResource(registrar);
    }

    @Path("urnNbnReservations")
    public UrnNbnReservationsResource getUrnNbnReservations() {
        return new UrnNbnReservationsResource(registrar);
    }
}
