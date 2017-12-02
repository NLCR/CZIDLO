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

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v5.exceptions.*;
import cz.nkp.urnnbn.api.v5.json.*;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv5.builders.*;
import cz.nkp.urnnbn.xml.apiv5.unmarshallers.DigitalInstanceUnmarshaller;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/digitalInstances")
public class DigitalInstancesResource extends ApiV5Resource {

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
    public Response getDigitalInstances(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
                                        @DefaultValue("") @QueryParam("url") String url) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            if (url.isEmpty()) {
                return buildDigitalInstancesCountRecord(format);
            } else {
                return buildDigitalInstancesByUrlRecords(format, url);
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format, e);
        }
    }

    private Response buildDigitalInstancesByUrlRecords(ResponseFormat format, String url) {
        List<DigitalInstance> digitalInstances = dataAccessService().digInstancesByUrl(url);
        switch (format) {
            case XML: {
                List<DigitalInstanceBuilderXml> diBuilders = createXmlInstanceBuilders(digitalInstances);
                DigitalInstancesBuilderXml xmlBuilder = new DigitalInstancesBuilderXml(diBuilders);
                String xml = xmlBuilder.buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                List<DigitalInstanceBuilderJson> diBuilders = createDigitalInstanceBuilders(digitalInstances);
                DigitalInstancesBuilderJson jsonBuilder = new DigitalInstancesBuilderJson(diBuilders);
                String json = jsonBuilder.toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            }
            default:
                throw new RuntimeException();
        }
    }

    private Response buildDigitalInstancesCountRecord(ResponseFormat format) {
        switch (format) {
            case XML: {
                DigitalInstancesBuilderXml xmlBuilder = null;
                if (digDoc == null) {
                    xmlBuilder = new DigitalInstancesBuilderXml(dataAccessService().digitalInstancesCount());
                } else {
                    List<DigitalInstanceBuilderXml> instanceBuilders = createXmlInstanceBuilders(digDoc);
                    xmlBuilder = new DigitalInstancesBuilderXml(instanceBuilders);
                }
                String xml = xmlBuilder.buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            }
            case JSON: {
                DigitalInstancesBuilderJson jsonBuilder = null;
                if (digDoc == null) {
                    jsonBuilder = new DigitalInstancesBuilderJson(dataAccessService().digitalInstancesCount());
                } else {
                    List<DigitalInstanceBuilderJson> instanceBuilders = createDigitalInstanceBuilders(digDoc);
                    jsonBuilder = new DigitalInstancesBuilderJson(instanceBuilders);
                }
                String json = jsonBuilder.toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
            }
            default:
                throw new RuntimeException();
        }
    }

    private List<DigitalInstanceBuilderXml> createXmlInstanceBuilders(List<DigitalInstance> digitalInstances) {
        List<DigitalInstanceBuilderXml> result = new ArrayList<>(digitalInstances.size());
        for (DigitalInstance instance : digitalInstances) {
            DigitalDocumentBuilderXml digDocBuilder = digitalDocumentBuilderXml(instance.getDigDocId());
            DigitalLibraryBuilderXml libBuilder = digitalLibraryBuilderXml(instance.getLibraryId());
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(instance, libBuilder, digDocBuilder);
            result.add(builder);
        }
        return result;
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

    private List<DigitalInstanceBuilderXml> createXmlInstanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilderXml> result = new ArrayList<>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    private List<DigitalInstanceBuilderJson> createDigitalInstanceBuilders(List<DigitalInstance> digitalInstances) {
        List<DigitalInstanceBuilderJson> result = new ArrayList<>(digitalInstances.size());
        for (DigitalInstance instance : digitalInstances) {
            DigitalDocumentBuilderJson digDocBuilder = createDigitalDocumentBuilderJson(instance.getDigDocId());
            DigitalLibraryBuilderJson libBuilder = createDigitalLibraryBuilderJson(instance.getLibraryId());
            DigitalInstanceBuilderJson builder = new DigitalInstanceBuilderJson(instance, libBuilder, digDocBuilder);
            result.add(builder);
        }
        return result;
    }

    private DigitalDocumentBuilderJson createDigitalDocumentBuilderJson(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilderJson idsBuilder = registrarScopeIdentifiersBuilderJson(digDocId);
        return new DigitalDocumentBuilderJson(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilderJson createDigitalLibraryBuilderJson(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilderJson regBuilder = new RegistrarBuilderJson(registrar, null, null);
        return new DigitalLibraryBuilderJson(library, regBuilder);
    }

    private List<DigitalInstanceBuilderJson> createDigitalInstanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilderJson> result = new ArrayList<>(instances.size());
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
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            if (digDoc == null) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            String login = req.getRemoteUser();
            Document requestXmlData = ApiModuleConfiguration.instanceOf().getDigInstImportDataValidatingLoaderV5().loadDocument(content);
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
