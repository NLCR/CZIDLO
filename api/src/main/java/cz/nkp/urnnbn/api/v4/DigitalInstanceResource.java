/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v4;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifiersBuilder;

public class DigitalInstanceResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(DigitalInstanceResource.class.getName());

    private final DigitalInstance instance;

    public DigitalInstanceResource(DigitalInstance instance) {
        this.instance = instance;
    }

    @GET
    @Produces("application/xml")
    public String getDigitalInstanceXmlRecord() {
        try {
            return getDigitalInstanceApiV4XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            return deactivateDigitalInstance(login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    public final String getDigitalInstanceApiV4XmlRecord() {
        return digitalInstanceXmlBuilder(instance, true, true).buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstanceBuilder digitalInstanceXmlBuilder(DigitalInstance instance, boolean withDigDoc, boolean withDigLib) {
        DigitalDocumentBuilder digDocBuilder = withDigDoc ? digDocBuilder(instance.getDigDocId()) : null;
        DigitalLibraryBuilder libBuilder = withDigLib ? digLibBuilder(instance.getLibraryId()) : null;
        return new DigitalInstanceBuilder(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilder digDocBuilder(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilder(digDocId);
        return new DigitalDocumentBuilder(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    private String deactivateDigitalInstance(String login) {
        DigitalInstance found = dataAccessService().digInstanceByInternalId(instance.getId());
        if (!found.isActive()) {
            throw new DigitalInstanceAlreadyDeactivatedException(instance);
        } else {
            deactivateDigitalInstanceWithServiceExceptionTranslation(login);
            DigitalInstance deactivated = dataAccessService().digInstanceByInternalId(instance.getId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(deactivated, deactivated.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
        }
    }

    private void deactivateDigitalInstanceWithServiceExceptionTranslation(String login) {
        try {
            dataRemoveService().deactivateDigitalInstance(instance.getId(), login);
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigInstException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }
}
