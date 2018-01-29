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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import cz.nkp.urnnbn.api.v4.exceptions.IncorrectPredecessorException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidArchiverIdException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidUrnException;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v4.exceptions.UnauthorizedRegistrationModeException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownUrnException;
import cz.nkp.urnnbn.api.v4.exceptions.UrnNbnDeactivatedException;
import cz.nkp.urnnbn.api.v4.json.DigitalDocumentsBuilderJson;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IncorrectPredecessorStatus;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrationModeNotAllowedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentsBuilderXml;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnBuilder;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.RecordImportUnmarshaller;

public class DigitalDocumentsResource extends AbstractDigitalDocumentResource {

    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentsResource.class.getName());

    private final Registrar registrar;

    public DigitalDocumentsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @Path("registrarScopeIdentifier/{idType}/{idValue}/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("idType") String idTypeStr, @PathParam("idValue") String idValueStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            DigitalDocument digitalDocument = getDigitalDocument(ResponseFormat.XML, idTypeStr, idValueStr);
            UrnNbn urnNbn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            UrnNbnWithStatus withStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(),
                    true);
            switch (withStatus.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                return new DigitalInstancesResource(digitalDocument);
            case FREE:
                throw new UnknownUrnException(format, urnNbn);
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, urnNbn);
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

    @Path("registrarScopeIdentifier/{idType}/{idValue}/registrarScopeIdentifiers")
    public RegistrarScopeIdentifiersResource getRegistrarScopeIdentifiersResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("idType") String idTypeStr, @PathParam("idValue") String idValueStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            DigitalDocument digitalDocument = getDigitalDocument(ResponseFormat.XML, idTypeStr, idValueStr);
            UrnNbn urnNbn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            UrnNbnWithStatus withStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(),
                    true);
            switch (withStatus.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                return new RegistrarScopeIdentifiersResource(digitalDocument);
            case FREE:
                throw new UnknownUrnException(format, urnNbn);
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, urnNbn);
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

    @GET
    @Path("registrarScopeIdentifier/{idType}/{idValue}")
    public Response getDigitalDocumentResource(@Context HttpServletRequest context, @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("idType") String idTypeStr, @PathParam("idValue") String idValueStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr) {
        if (formatStr == null) {
            // allways redirect somwehere
            DigitalDocument digitalDocument = getDigitalDocument(ResponseFormat.XML, idTypeStr, idValueStr);
            UrnNbn urn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            return redirectionResponse(context, urn);
        } else {
            // show data
            ResponseFormat format = formatStr == null ? ResponseFormat.XML : Parser.parseFormat(formatStr);
            boolean withDigitalInstances = Parser.parseBooleanQueryParam(format, withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            DigitalDocument digitalDocument = getDigitalDocument(format, idTypeStr, idValueStr);
            UrnNbn urn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            return metadataResponse(urn, format, withDigitalInstances);
        }
    }

    private Response redirectionResponse(HttpServletRequest context, UrnNbn urnNbnParsed) {
        try {
            try {
                UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnParsed.getRegistrarCode(),
                        urnNbnParsed.getDocumentCode(), true);
                switch (fetched.getStatus()) {
                case ACTIVE:
                    return redirectionResponse(fetched.getUrn(), context.getHeader(HEADER_REFERER));
                default:
                    // redirect to web client
                    return Response.seeOther(buildWebSearchUri(urnNbnParsed.toString())).build();
                }
            } catch (WebApplicationException e) {
                // redirect to web client
                return Response.seeOther(buildWebSearchUri(urnNbnParsed.toString())).build();
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(ResponseFormat.XML, e.getMessage());
        }
    }

    private Response redirectionResponse(UrnNbn urnNbn, String referer) throws URISyntaxException {
        if (urnNbn.isActive()) {
            URI digitalInstance = getAvailableActiveDigitalInstanceOrNull(urnNbn.getDigDocId(), null, referer);
            if (digitalInstance != null) {
                // redirect to DI
                return Response.seeOther(digitalInstance).build();
            }
        }
        // otherwise redirect to web client
        return Response.seeOther(buildWebSearchUri(urnNbn.toString())).build();
    }

    private Response metadataResponse(UrnNbn urn, ResponseFormat format, boolean withDigitalInstances) {
        try {
            UrnNbnWithStatus urnNbnWithState = dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(),
                    true);
            switch (urnNbnWithState.getStatus()) {
            case ACTIVE:
                DigitalDocument doc = dataAccessService().digDocByInternalId(urnNbnWithState.getUrn().getDigDocId());
                if (doc == null) {
                    throw new UnknownDigitalDocumentException(null, urnNbnWithState.getUrn());
                } else {
                    return metadataResponse(doc, urnNbnWithState.getUrn(), format, withDigitalInstances);
                }
            case DEACTIVATED:
                throw new UrnNbnDeactivatedException(format, urnNbnWithState.getUrn());
            case FREE:
                throw new UnknownUrnException(format, urnNbnWithState.getUrn());
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, urnNbnWithState.getUrn());
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

    private Response metadataResponse(DigitalDocument doc, UrnNbn urnNbn, ResponseFormat format, boolean withDigitalInstances) {
        switch (format) {
        case XML:
            String xml = digitalDocumentBuilderXml(doc, urnNbn, withDigitalInstances).buildDocumentWithResponseHeader().toXML();
            return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
        case JSON:
            String json = digitalDocumentBuilderJson(doc, urnNbn, withDigitalInstances).toJson();
            return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
        default:
            throw new RuntimeException();
        }
    }

    private DigitalDocument getDigitalDocument(ResponseFormat format, String idTypeStr, String idValueStr) {
        RegistrarScopeIdType type = Parser.parseRegistrarScopeIdType(format, idTypeStr);
        RegistrarScopeIdValue value = Parser.parseRegistrarScopeIdValue(format, idValueStr);
        RegistrarScopeIdentifier id = new RegistrarScopeIdentifier();
        id.setRegistrarId(registrar.getId());
        id.setType(type);
        id.setValue(value);
        DigitalDocument digDoc = dataAccessService().digDocByIdentifier(id);
        if (digDoc == null) {
            throw new UnknownDigitalDocumentException(format, registrar.getCode(), type, value);
        } else {
            return digDoc;
        }
    }

    @GET
    public Response getDigitalDocumentsRecord(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            switch (format) {
            case XML:
                String xml = digitalDocumentsBuilderXml().buildDocumentWithResponseHeader().toXML();
                return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
            case JSON:
                String json = digitalDocumentsBuilderJson().toJson();
                return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
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

    private DigitalDocumentsBuilderXml digitalDocumentsBuilderXml() {
        int digDocsCount = dataAccessService().digitalDocumentsCount(registrar.getId());
        return new DigitalDocumentsBuilderXml(digDocsCount);
    }

    private DigitalDocumentsBuilderJson digitalDocumentsBuilderJson() {
        int digDocsCount = dataAccessService().digitalDocumentsCount(registrar.getId());
        return new DigitalDocumentsBuilderJson(digDocsCount);
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response registerDigitalDocument(@Context HttpServletRequest context, String content) {
        // TODO:APIv5: response format should not be fixed to XML but rather negotiated through Accept header
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            String login = context.getRemoteUser();
            String response = registerDigitalDocumentReturnXml(format, content, login, registrar.getCode());
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

    private String registerDigitalDocumentReturnXml(ResponseFormat format, String content, String login, RegistrarCode registrarCode)
            throws ValidityException, IOException, ParsingException {
        Document doc = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV4().loadDocument(content);
        return registerDigitalDocumentReturnXml(format, doc, login, registrarCode);
    }

    private String registerDigitalDocumentReturnXml(ResponseFormat format, Document doc, String login, RegistrarCode registrarCode) {
        try {
            DigDocRegistrationData registrationData = digDocRegistrationDataFromDoc(doc);
            UrnNbn urnInData = registrationData.getUrn();
            if (urnInData != null && !urnInData.getRegistrarCode().toString().equals(registrarCode.toString())) {
                throw new InvalidUrnException(format, urnInData.toString(), "Doesn't match expected registrar code '" + registrarCode.toString()
                        + "'");
            }
            UrnNbn urn = dataImportService().registerDigitalDocument(registrationData, login);
            UrnNbnWithStatus withStatus = getUrnWithStatus(urn, true);
            UrnNbnBuilder builder = new UrnNbnBuilder(withStatus);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (IncorrectPredecessorStatus ex) {
            throw new IncorrectPredecessorException(format, ex.getPredecessor());
        } catch (RegistrationModeNotAllowedException ex) {
            throw new UnauthorizedRegistrationModeException(format, ex.getMode(), ex.getUrn(), registrar);
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (UnknownArchiverException ex) {
            throw new InvalidArchiverIdException(format, ex.getMessage());
        } catch (RegistrarScopeIdentifierCollisionException ex) {
            throw new cz.nkp.urnnbn.api.v4.exceptions.RegistrarScopeIdentifierCollisionException(format, ex.getMessage());
        } catch (UrnNotFromRegistrarException ex) {
            throw new InvalidUrnException(format, ex.getUrn().toString(), ex.getMessage());
        } catch (UrnUsedException ex) {
            throw new InvalidUrnException(format, ex.getUrn().toString(), ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            LOGGER.log(Level.SEVERE, "unexpected application state", ex);
            throw new InternalException(format, ex);
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        }
    }

    private DigDocRegistrationData digDocRegistrationDataFromDoc(Document doc) {
        RecordImportUnmarshaller unmarshaller = new RecordImportUnmarshaller(doc);
        DigDocRegistrationData result = new DigDocRegistrationData();
        // intelectual entity
        result.setEntity(unmarshaller.getIntelectualEntity());
        result.setIntEntIds(unmarshaller.getIntEntIdentifiers());
        result.setOriginator(unmarshaller.getOriginator());
        result.setPublication(unmarshaller.getPublication());
        result.setOriginator(unmarshaller.getOriginator());
        result.setSourceDoc(unmarshaller.getSourceDocument());
        // registrar
        result.setRegistrarCode(registrar.getCode());
        // archiver
        Long archiverId = unmarshaller.getArchiverId() == null ? registrar.getId() : unmarshaller.getArchiverId();
        // digital document
        DigitalDocument digDoc = unmarshaller.getDigitalDocument();
        digDoc.setRegistrarId(registrar.getId());
        digDoc.setArchiverId(archiverId);
        result.setDigitalDocument(digDoc);
        result.setDigDocIdentifiers(unmarshaller.getRegistrarScopeIdentifiers());
        // urn:nbn
        result.setUrn(unmarshaller.getUrnNbn());
        // predecessors
        result.setPredecessors(appendStatuses(unmarshaller.getPredecessors()));
        return result;
    }

    private UrnNbnWithStatus getUrnWithStatus(UrnNbn urn, boolean withPredecessorsAndSuccessors) {
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), withPredecessorsAndSuccessors);
    }

    private List<UrnNbnWithStatus> appendStatuses(List<UrnNbnWithStatus> predecessors) {
        List<UrnNbnWithStatus> result = new ArrayList<UrnNbnWithStatus>(predecessors.size());
        for (UrnNbnWithStatus urn : predecessors) {
            UrnNbnWithStatus withCorrectStatus = getUrnWithStatus(urn.getUrn(), false);
            result.add(new UrnNbnWithStatus(withCorrectStatus.getUrn(), withCorrectStatus.getStatus(), urn.getNote()));
        }
        return result;
    }

}
