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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.v4.exceptions.IllegalFormatError;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.JsonVersionNotImplementedError;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
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
    public Response getDigitalInstanceXmlRecord(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        if (format == ResponseFormat.JSON) { // TODO: remove when implemented
            throw new JsonVersionNotImplementedError(format);
        }
        try {
            switch (format) {
            case XML: {
                String xml = digitalInstanceXmlBuilder().buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                // TODO: implement json version
                throw new JsonVersionNotImplementedError(format);
            }
            default:
                throw new IllegalFormatError(ResponseFormat.XML, formatStr);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private DigitalInstanceBuilder digitalInstanceXmlBuilder() {
        DigitalDocumentBuilder digDocBuilder = digitalDocumentXmlBuilder(instance.getDigDocId());
        DigitalLibraryBuilder libBuilder = digitalLibraryXmlBuilder(instance.getLibraryId());
        return new DigitalInstanceBuilder(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilder digitalDocumentXmlBuilder(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilder(digDocId);
        return new DigitalDocumentBuilder(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digitalLibraryXmlBuilder(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        ResponseFormat format = ResponseFormat.XML;// TODO: parse format, support xml and json
        try {
            checkServerNotReadOnly(format);
            String login = req.getRemoteUser();
            return deactivateDigitalInstanceReturnXml(format, login);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private String deactivateDigitalInstanceReturnXml(ResponseFormat format, String login) {
        DigitalInstance found = dataAccessService().digInstanceByInternalId(instance.getId());
        if (!found.isActive()) {
            throw new DigitalInstanceAlreadyDeactivatedException(format, instance);
        } else {
            deactivateDigitalInstanceWithServiceExceptionTranslation(format, login);
            DigitalInstance deactivated = dataAccessService().digInstanceByInternalId(instance.getId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(deactivated, deactivated.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
        }
    }

    private void deactivateDigitalInstanceWithServiceExceptionTranslation(ResponseFormat format, String login) {
        try {
            dataRemoveService().deactivateDigitalInstance(instance.getId(), login);
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownDigInstException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(format, ex);
        }
    }
}
