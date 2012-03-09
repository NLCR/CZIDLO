/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.rest.config.ApiConfiguration;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

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
            DigitalDocumentBuilder digRepBuilder = digRepBuilder(instance.getDigDocId());
            DigitalLibraryBuilder libBuilder = digLibBuilder(instance.getLibraryId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, libBuilder, digRepBuilder);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalDocumentBuilder digRepBuilder(long digRepId) throws DatabaseException {
        DigitalDocument digRep = dataAccessService().digDocByInternalId(digRepId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digRep.getId());
        DigitalDocumentIdentifiersBuilder idsBuilder = digRepIdentifiersBuilder(digRepId);
        return new DigitalDocumentBuilder(digRep, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) throws DatabaseException {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    @DELETE
    @Produces("application/xml")
    public String removeDigitalInstance(@Context HttpServletRequest req) {
        if (ApiConfiguration.instanceOf().isServerReadOnly()) {
            throw new MethodForbiddenException();
        } else {
            return "<TODO>implementovat</TODO>";
        }
    }
}
