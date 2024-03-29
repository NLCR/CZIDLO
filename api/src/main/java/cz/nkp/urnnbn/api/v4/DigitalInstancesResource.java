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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.api.v4.json.DigitalInstanceBuilderJson;
import cz.nkp.urnnbn.api.v4.json.DigitalInstancesBuilderJson;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilderXml;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilderXml;
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

    @Path("id/{digInstId}")
    public DigitalInstanceResource getDigitalInstanceResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("digInstId") String digInstIdStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            long id = Parser.parseDigInstId(format, digInstIdStr);
            DigitalInstance digitalInstance = getDigitalInstance(format, id);
            return new DigitalInstanceResource(digitalInstance);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private DigitalInstance getDigitalInstance(ResponseFormat format, long digitalInstanceId) {
        DigitalInstance instance = dataAccessService().digInstanceByInternalId(digitalInstanceId);
        if (instance == null) {
            throw new UnknownDigitalInstanceException(format, digitalInstanceId);
        } else {
            return instance;
        }
    }

    @GET
    public Response getDigitalInstances(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            switch (format) {
            case XML: {
                String xml = xmlInstancesBuilder().buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                String json = jsonInstancesBuilder().toJson();
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

    private DigitalInstancesBuilderXml xmlInstancesBuilder() {
        if (digDoc == null) {
            return new DigitalInstancesBuilderXml(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilderXml> instanceBuilders = xmlInstanceBuilders(digDoc);
            return new DigitalInstancesBuilderXml(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilderXml> xmlInstanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilderXml> result = new ArrayList<DigitalInstanceBuilderXml>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    private DigitalInstancesBuilderJson jsonInstancesBuilder() {
        if (digDoc == null) {
            return new DigitalInstancesBuilderJson(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilderJson> instanceBuilders = jsonInstanceBuilders(digDoc);
            return new DigitalInstancesBuilderJson(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilderJson> jsonInstanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilderJson> result = new ArrayList<DigitalInstanceBuilderJson>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilderJson builder = new DigitalInstanceBuilderJson(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response addNewDigitalInstance(@Context HttpServletRequest req, String content) {
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document requestXmlData = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV4().loadDocument(content);
            DigitalInstance digitalInstance = parseDigitalInstanceFromXml(requestXmlData);
            String response = addNewDigitalInstanceWithXmlResponse(format, digitalInstance, login);
            return Response.created(null).entity(response).build();
        } catch (ValidityException ex) {
            throw new InvalidDataException(format, ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(format, ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private DigitalInstance parseDigitalInstanceFromXml(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    private String addNewDigitalInstanceWithXmlResponse(ResponseFormat format, DigitalInstance digitalInstance, String login) {
        try {
            Parser.parseUrl(format, digitalInstance.getUrl());
            checkNoOtherDigInstInSameLibraryPresent(format, digitalInstance);
            DigitalInstance digInstInserted = dataImportService().addDigitalInstance(digitalInstance, login);
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(digInstInserted, digitalInstance.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
            // String responseXml = builder.buildDocumentWithResponseHeader().toXML();
            // return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownDigLibException ex) {
            throw new UnknownDigitalLibraryException(format, ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, null, ex);
            throw new InternalException(format, ex);
        }
    }

    private void checkNoOtherDigInstInSameLibraryPresent(ResponseFormat format, DigitalInstance digInstFromClient) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digInstFromClient.getDigDocId());
        for (DigitalInstance instance : instances) {
            if (instance.isActive() && instance.getLibraryId().equals(digInstFromClient.getLibraryId())) {
                throw new DigitalInstanceAlreadyPresentException(format, instance);
            }
        }
    }

}
