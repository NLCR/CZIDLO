/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.api.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
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
        return xmlBuilder(instance, true, true).buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstanceBuilder xmlBuilder(DigitalInstance instance, boolean withDigDoc, boolean withDigLib) {
        try {
            DigitalDocumentBuilder digDocBuilder = withDigDoc ? digDocBuilder(instance.getDigDocId()) : null;
            DigitalLibraryBuilder libBuilder = withDigLib ? digLibBuilder(instance.getLibraryId()) : null;
            return new DigitalInstanceBuilder(instance, libBuilder, digDocBuilder);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalDocumentBuilder digDocBuilder(long digDocId) throws DatabaseException {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilder(digDocId);
        return new DigitalDocumentBuilder(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) throws DatabaseException {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        if (ApiModuleConfiguration.instanceOf().isServerReadOnly()) {
            throw new MethodForbiddenException();
        } else {
            try {
                String login = req.getRemoteUser();
                DigitalInstance found = dataAccessService().digInstanceByInternalId(instance.getId());
                if (!found.isActive()) {
                    throw new DigitalInstanceAlreadyDeactivatedException(instance);
                } else {
                    dataRemoveService().deactivateDigitalInstance(instance.getId(), login);
                    DigitalInstance deactivated = dataAccessService().digInstanceByInternalId(instance.getId());
                    DigitalInstanceBuilder builder = new DigitalInstanceBuilder(deactivated, deactivated.getLibraryId());
                    return builder.buildDocumentWithResponseHeader().toXML();
                }
            } catch (UnknownUserException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (AccessException ex) {
                throw new NotAuthorizedException(ex.getMessage());
            } catch (UnknownDigInstException ex) {
                //should never happen
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex);
            } catch (WebApplicationException e) {
                throw e;
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex.getMessage());
            }
        }
    }
}
