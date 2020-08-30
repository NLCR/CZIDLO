package cz.nkp.urnnbn.api.v4;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.NoAccessRightsException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownUrnException;
import cz.nkp.urnnbn.api.v4.exceptions.UrnNbnDeactivatedException;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.RecordImportUnmarshaller;

@Path("/resolver")
public class UrnNbnResolverResource extends AbstractDigitalDocumentResource {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnResolverResource.class.getName());

    @Path("{urn}/registrarScopeIdentifiers")
    public RegistrarScopeIdentifiersResource getRegistrarScopeIdentifiersResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("urn") String urnNbnString) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            UrnNbn urnNbnParsed = Parser.parseUrn(format, urnNbnString);
            UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnParsed.getRegistrarCode(),
                    urnNbnParsed.getDocumentCode(), true);
            switch (fetched.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                DigitalDocument doc = dataAccessService().digDocByInternalId(fetched.getUrn().getDigDocId());
                if (doc == null) {
                    throw new UnknownDigitalDocumentException(format, fetched.getUrn());
                } else {
                    return new RegistrarScopeIdentifiersResource(doc);
                }
            case FREE:
                throw new UnknownUrnException(format, urnNbnParsed);
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, fetched.getUrn());
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

    @Path("{urn}/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource(@DefaultValue("xml") @QueryParam(PARAM_FORMAT) String formatStr,
            @PathParam("urn") String urnNbnString) {
        ResponseFormat format = Parser.parseFormat(formatStr);
        try {
            UrnNbn urnNbnParsed = Parser.parseUrn(format, urnNbnString);
            UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnParsed.getRegistrarCode(),
                    urnNbnParsed.getDocumentCode(), true);
            switch (fetched.getStatus()) {
            case DEACTIVATED:
            case ACTIVE:
                DigitalDocument digDoc = dataAccessService().digDocByInternalId(fetched.getUrn().getDigDocId());
                if (digDoc == null) {
                    throw new UnknownDigitalDocumentException(format, fetched.getUrn());
                } else {
                    return new DigitalInstancesResource(digDoc);
                }
            case FREE:
                throw new UnknownUrnException(format, urnNbnParsed);
            case RESERVED:
                throw new UnknownDigitalDocumentException(format, fetched.getUrn());
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
    @Path("{urn}")
    public Response resolve(@Context HttpServletRequest context, @PathParam("urn") String urnNbnString, @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr) {
        if (formatStr == null) {
            // always redirect somewhere
            return redirectionResponse(context, urnNbnString);
        } else {
            // show data
            ResponseFormat format = Parser.parseFormat(formatStr);
            boolean withDigitalInstances = Parser.parseBooleanQueryParam(format, withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            return metadataResponse(urnNbnString, format, withDigitalInstances);
        }
    }

    private Response redirectionResponse(HttpServletRequest context, String urnNbnString) {
        try {
            try {
                UrnNbn urnNbnParsed = Parser.parseUrn(ResponseFormat.XML, urnNbnString);
                UrnNbnWithStatus fetched = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnParsed.getRegistrarCode(),
                        urnNbnParsed.getDocumentCode(), true);
                switch (fetched.getStatus()) {
                case ACTIVE:
                    return redirectionResponse(fetched.getUrn(), context.getHeader(HEADER_REFERER));
                default:
                    // redirect to web client
                    return Response.seeOther(buildWebSearchUri(urnNbnString)).build();
                }
            } catch (WebApplicationException e) {
                // redirect to web client
                return Response.seeOther(buildWebSearchUri(urnNbnString)).build();
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(ResponseFormat.XML, e.getMessage());
        }
    }

    private Response redirectionResponse(UrnNbn urnNbn, String referer) throws URISyntaxException {
        // update resolvations statistics
        statisticService().incrementResolvationStatistics(urnNbn.getRegistrarCode().toString());
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

    private Response metadataResponse(String urnNbnString, ResponseFormat format, boolean withDigitalInstances) {
        try {
            UrnNbnWithStatus urnNbnWithState = parse(urnNbnString, format);
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

    private UrnNbnWithStatus parse(String urnNbnString, ResponseFormat format) {
        UrnNbn urnNbnParsed = Parser.parseUrn(format, urnNbnString);
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbnParsed.getRegistrarCode(), urnNbnParsed.getDocumentCode(), true);
    }

    private Response metadataResponse(DigitalDocument doc, UrnNbn urnNbn, ResponseFormat format, boolean withDigitalInstances) {
        switch (format) {
        case XML: {
            String xml = digitalDocumentBuilderXml(doc, urnNbn, withDigitalInstances).buildDocumentWithResponseHeader().toXML();
            return Response.status(Status.OK).type(MediaType.APPLICATION_XML).entity(xml).build();
        }
        case JSON: {
            String json = digitalDocumentBuilderJson(doc, urnNbn, withDigitalInstances).toJson();
            return Response.status(Status.OK).type(JSON_WITH_UTF8).entity(json).build();
        }
        default:
            throw new RuntimeException();
        }
    }

    @PUT
    @Path("{urn}")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response updateDigitalDocument(@Context HttpServletRequest context, @PathParam("urn") String urnNbnString, String content) {
        ResponseFormat format = ResponseFormat.XML;
        try {
            checkServerNotReadOnly(format);
            String login = context.getRemoteUser();
            UrnNbnWithStatus urnNbnWithState = parse(urnNbnString, format);
            switch (urnNbnWithState.getStatus()) {
            case ACTIVE:
                DigitalDocument doc = dataAccessService().digDocByInternalId(urnNbnWithState.getUrn().getDigDocId());
                if (doc == null) {
                    throw new UnknownDigitalDocumentException(null, urnNbnWithState.getUrn());
                } else {
                    return updateDigitalDocumentReturnXml(format, content, login, urnNbnWithState.getUrn());
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

    private Response updateDigitalDocumentReturnXml(ResponseFormat format, String content, String login, UrnNbn urnNbn) throws ValidityException,
            ParsingException, IOException, UnknownUserException, AccessException, UnknownDigDocException, UnknownIntelectualEntity {
        try {
            Document xml = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV4().loadDocument(content);
            RecordImportUnmarshaller unmarshaller = new RecordImportUnmarshaller(xml);
            boolean modified = new EmptyDigDocAndIeFieldFiller(urnNbn, login).update(unmarshaller, format);
            DigitalDocument doc = dataAccessService().digDocByInternalId(urnNbn.getDigDocId());
            if (doc == null) {
                throw new UnknownDigitalDocumentException(null, urnNbn);
            } else {
                return metadataResponse(doc, urnNbn, format, true);
            }
        } catch (ValidityException ex) {
            throw new InvalidDataException(format, ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(format, ex);
        } catch (UnknownUserException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        } catch (AccessException ex) {
            throw new NoAccessRightsException(format, ex.getMessage());
        }
    }

}
