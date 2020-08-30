package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v5.exceptions.*;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv5.unmarshallers.RecordImportUnmarshaller;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/resolver")
public class ResolverResource extends AbstractDigitalDocumentResource {

    private static final Logger LOGGER = Logger.getLogger(ResolverResource.class.getName());

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
    @Path("{id}")
    public Response resolve(@Context HttpServletRequest context, @PathParam("id") String id, @QueryParam(PARAM_FORMAT) String formatStr,
                            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr) {
        if (isKnownForeignUrnNbn(id)) {
            return redirectionToForeignResolverResponse(context, id);
        } else if (formatStr == null) {
            // always redirect somwehere
            return redirectionResponse(context, id);
        } else {
            // show data
            ResponseFormat format = Parser.parseFormat(formatStr);
            boolean withDigitalInstances = Parser.parseBooleanQueryParam(format, withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            return metadataResponse(id, format, withDigitalInstances);
        }
    }

    private boolean isKnownForeignUrnNbn(String urnNbnString) {
        if (urnNbnString != null && !urnNbnString.isEmpty() && urnNbnString.toLowerCase().startsWith("urn:nbn:")) {
            String langCode = urnNbnString.split(":")[2].substring(0, 2).toLowerCase();
            String[] allowedCodes = new String[]{"de", "it", "fi", "se", "no", "hu", "nl", "si"};
            for (String allowedCode : allowedCodes) {
                if (langCode.equals(allowedCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Response redirectionToForeignResolverResponse(HttpServletRequest context, String id) {
        try {
            String urnNbnString = id.toLowerCase();
            String langCode = urnNbnString.split(":")[2].substring(0, 2).toLowerCase();
            switch (langCode) {
                case "de"://http://nbn-resolving.de/urn:nbn:de:101:1-200910131091
                    return Response.seeOther(new URI("http://nbn-resolving.de/" + urnNbnString)).build();
                case "it": //http://nbn.depositolegale.it/urn:nbn:it:unimi-6456
                    return Response.seeOther(new URI("http://nbn.depositolegale.it/" + urnNbnString)).build();
                case "fi": //http://urn.fi/urn:nbn:fi-fe20042357
                    return Response.seeOther(new URI("http://urn.fi/" + urnNbnString)).build();
                case "se": //http://urn.kb.se/resolve?urn=urn:nbn:se:su:diva-1278
                    return Response.seeOther(new URI("http://urn.kb.se/resolve?urn=" + urnNbnString)).build();
                case "no": //http://urn.nb.no/URN:NBN:no-nb_digibok_2010090303019
                    return Response.seeOther(new URI("http://urn.nb.no/" + urnNbnString)).build();
                case "hu": //http://nbn-test.urn.hu/N2L?urn:nbn:hu-107035
                    return Response.seeOther(new URI("http://nbn-test.urn.hu/N2L?" + urnNbnString)).build();
                case "nl": //http://persistent-identifier.nl/?identifier=URN:NBN:NL:UI:10-1-115852
                    return Response.seeOther(new URI("http://persistent-identifier.nl/?identifier=" + urnNbnString)).build();
                case "si": //http://www.nbn.si/URN:NBN:SI:FSD:MAG-1DC22KA
                    return Response.seeOther(new URI("http://www.nbn.si/" + urnNbnString)).build();
                default:
                    return Response.status(Status.FORBIDDEN).build();
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(ResponseFormat.XML, e.getMessage());
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
            URI digitalInstance = getAvailableActiveDigitalInstanceOrNull(urnNbn.getDigDocId(), referer);
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
            Document xml = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV5().loadDocument(content);
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
