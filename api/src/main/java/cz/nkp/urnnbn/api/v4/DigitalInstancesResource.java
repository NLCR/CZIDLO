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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.DigitalInstanceUnmarshaller;

@Path("/digitalInstances")
public class DigitalInstancesResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(DigitalInstancesResource.class.getName());

    private final DigitalDocument digDoc;

    public DigitalInstancesResource(DigitalDocument digDoc) {
        this.digDoc = digDoc;
    }

    public DigitalInstancesResource() {
        this(null);
    }

    @GET
    @Produces("application/xml")
    public String getDigitalInstancesXmlRecord() {
        try {
            return getDigitalInstancesApiV4XmlRecord();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @Path("id/{digInstId}")
    public DigitalInstanceResource getDigitalInstanceResource(@PathParam("digInstId") String digInstIdStr) {
        try {
            long id = Parser.parseDigInstId(digInstIdStr);
            DigitalInstance digitalInstance = getDigitalInstance(id);
            return new DigitalInstanceResource(digitalInstance);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(@Context HttpServletRequest req, String content) {
        try {
            checkServerNotReadOnly();
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document requestXmlData = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV4().loadDocument(content);
            DigitalInstance digitalInstance = digitalInstanceFromApiV4Document(requestXmlData);
            String response = addNewDigitalInstanceWithApiV4Response(digitalInstance, login);
            return Response.created(null).entity(response).build();
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String getDigitalInstancesApiV4XmlRecord() {
        DigitalInstancesBuilder builder = instancesBuilder();
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstancesBuilder instancesBuilder() {
        if (digDoc == null) {
            return new DigitalInstancesBuilder(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilder> instanceBuilders = instanceBuilders(digDoc);
            return new DigitalInstancesBuilder(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilder> instanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    private DigitalInstance digitalInstanceFromApiV4Document(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    private String addNewDigitalInstanceWithApiV4Response(DigitalInstance digitalInstance, String login) {
        try {
            Parser.parseUrl(digitalInstance.getUrl());
            checkNoOtherDigInstInSameLibraryPresent(digitalInstance);
            DigitalInstance digInstInserted = dataImportService().addDigitalInstance(digitalInstance, login);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(digInstInserted, digitalInstance.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
            // String responseXml = builder.buildDocumentWithResponseHeader().toXML();
            // return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigLibException ex) {
            throw new UnknownDigitalLibraryException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        }
    }

    private void checkNoOtherDigInstInSameLibraryPresent(DigitalInstance digInstFromClient) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digInstFromClient.getDigDocId());
        for (DigitalInstance instance : instances) {
            if (instance.isActive() && instance.getLibraryId().equals(digInstFromClient.getLibraryId())) {
                throw new DigitalInstanceAlreadyPresentException(instance);
            }
        }
    }

    private DigitalInstance getDigitalInstance(long digitalInstanceId) {
        DigitalInstance instance = dataAccessService().digInstanceByInternalId(digitalInstanceId);
        if (instance == null) {
            throw new UnknownDigitalInstanceException(digitalInstanceId);
        } else {
            return instance;
        }
    }
}
