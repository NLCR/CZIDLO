/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidMethodException;
import cz.nkp.urnnbn.rest.exceptions.InvalidMethodException.MethodAllowed;
import cz.nkp.urnnbn.rest.exceptions.InvalidSiglaException;
import cz.nkp.urnnbn.rest.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.xml.builders.RegistrarsBuilder;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/registrars")
public class RegistrarsResource extends Resource {

    //private static MethodAllowed[] allowedMethods = {MethodAllowed.GET};
    @Context
    private UriInfo context;

    /**
     * Retrieves representation of an instance of cz.nkp.urnnbn.rest.RegistrarsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        try {
            List<Registrar> registrars = dataAccessService().registrars();
            RegistrarsBuilder builder = new RegistrarsBuilder(registrars);
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
            Sigla sigla = parseSigla(siglaStr);
            Registrar registrar = dataAccessService().registrarBySigla(sigla);
            if (registrar == null) {
                throw new UnknownRegistrarException(sigla);
            }
            return new RegistrarResource(registrar);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private Sigla parseSigla(String siglaStr) {
        try {
            return Sigla.valueOf(siglaStr);
        } catch (RuntimeException e) {
            throw new InvalidSiglaException(siglaStr);
        }
    }
//    @Produces("application/xml")
//    @POST
//    public String post() {
//        throw new InvalidMethodException(allowedMethods);
//    }
//
//    @Produces("application/xml")
//    @PUT
//    public String put() {
//        throw new InvalidMethodException(allowedMethods);
//    }
//
//    @Produces("application/xml")
//    @DELETE
//    public String delete() {
//        throw new InvalidMethodException(allowedMethods);
//    }
}
