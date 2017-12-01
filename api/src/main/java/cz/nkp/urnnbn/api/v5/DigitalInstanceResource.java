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
package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.v5.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.v5.exceptions.InternalException;
import cz.nkp.urnnbn.api.v5.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v5.json.*;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv5.builders.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigitalInstanceResource extends ApiV5Resource {

    private static final Logger LOGGER = Logger.getLogger(DigitalInstanceResource.class.getName());

    private final DigitalInstance instance;

    public DigitalInstanceResource(DigitalInstance instance) {
        this.instance = instance;
    }

    @GET
    public Response getDigitalInstance(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            switch (format) {
            case XML: {
                String xml = digitalInstanceBuilderXml().buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                String json = digitalInstanceBuilderJson().toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            }
            default:
                throw new RuntimeException();
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private DigitalInstanceBuilderXml digitalInstanceBuilderXml() {
        DigitalDocumentBuilderXml digDocBuilder = digitalDocumentBuilderXml(instance.getDigDocId());
        DigitalLibraryBuilderXml libBuilder = digitalLibraryBuilderXml(instance.getLibraryId());
        return new DigitalInstanceBuilderXml(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilderXml digitalDocumentBuilderXml(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilderXml(digDocId);
        return new DigitalDocumentBuilderXml(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilderXml digitalLibraryBuilderXml(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilderXml(library, regBuilder);
    }

    private DigitalInstanceBuilderJson digitalInstanceBuilderJson() {
        DigitalDocumentBuilderJson digDocBuilder = digitalDocumentBuilderJson(instance.getDigDocId());
        DigitalLibraryBuilderJson libBuilder = digitalLibraryBuilderJson(instance.getLibraryId());
        return new DigitalInstanceBuilderJson(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilderJson digitalDocumentBuilderJson(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilderJson idsBuilder = registrarScopeIdentifiersBuilderJson(digDocId);
        return new DigitalDocumentBuilderJson(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilderJson digitalLibraryBuilderJson(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilderJson regBuilder = new RegistrarBuilderJson(registrar, null, null);
        return new DigitalLibraryBuilderJson(library, regBuilder);
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
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
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(deactivated, deactivated.getLibraryId());
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
