/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.logging.Level;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceResource extends Resource {

    private final DigitalInstance instance;

    public DigitalInstanceResource(DigitalInstance instance) {
        this.instance = instance;
    }

    @GET
    @Produces("application/xml")
    public String getDigitalInstance() {
        try {
            DigitalRepresentationBuilder digRepBuilder = digRepBuilder(instance.getDigRepId());
            DigitalLibraryBuilder libBuilder = digLibBuilder(instance.getLibraryId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, libBuilder, digRepBuilder);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalRepresentationBuilder digRepBuilder(long digRepId) throws DatabaseException {
        DigitalRepresentation digRep = dataAccessService().digRepByInternalId(digRepId);
        UrnNbn urn = dataAccessService().urnByDigRepId(digRep.getId());
        DigitalRepresentationIdentifiersBuilder idsBuilder = digRepIdentifiersBuilder(digRepId);
        return new DigitalRepresentationBuilder(digRep, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) throws DatabaseException {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    @DELETE
    @Produces("application/xml")
    public String removeDigitalInstance() {
        if (Config.SERVER_READ_ONLY) {
            throw new MethodForbiddenException();
        } else {
            return "<TODO>implementovat</TODO>";
        }
    }
}
