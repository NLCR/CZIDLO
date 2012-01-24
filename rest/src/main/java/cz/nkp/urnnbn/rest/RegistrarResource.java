/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.xml.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class RegistrarResource extends Resource {

    private Registrar registrar;

    /** Creates a new instance of RegistrarResource */
    public RegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * Retrieves representation of an instance of cz.nkp.urnnbn.rest.RegistrarResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getRegistrar(
            @DefaultValue("true") @QueryParam("digitalLibraries") boolean addDigitalLibraries,
            @DefaultValue("true") @QueryParam("catalogs") boolean addCatalogs) {
        try {
            DigitalLibrariesBuilder libBuilder = addDigitalLibraries
                    ? librariesBuilder() : null;
            CatalogsBuilder catBuilder = addCatalogs ? catalogsBuilder() : null;
            return new RegistrarBuilder(registrar, libBuilder, catBuilder).buildDocument().toXML();
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

    /**
     * PUT method for updating or creating an instance of RegistrarResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }

    /**
     * DELETE method for resource RegistrarResource
     */
    @DELETE
    public void delete() {
    }

    private DigitalLibrariesBuilder librariesBuilder() throws DatabaseException {
        List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrar(registrar.getId());
        return new DigitalLibrariesBuilder(libraries);
    }

    private CatalogsBuilder catalogsBuilder() throws DatabaseException {
        List<Catalog> catalogs = dataAccessService().catalogsByRegistrar(registrar.getId());
        return new CatalogsBuilder(catalogs);
    }
}
