/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.config.Configuration;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.xml.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentResource extends Resource {

    private static final String PARAM_ACTION = "action";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_WITH_DIG_INST = "digitalInstances";
    private static final String WEB_MODULE_CONTEXT = "web";
    private static final String HEADER_REFERER = "referer";
    private final DigitalDocument doc;
    private UrnNbn urn;

    public DigitalDocumentResource(DigitalDocument doc, UrnNbn urn) {
        this.doc = doc;
        this.urn = urn;
    }

    @GET
    @Produces("application/xml")
    public Response resolve(
            @DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr,
            @Context HttpServletRequest request) {
        Action action = Parser.parseAction(actionStr, PARAM_ACTION);
        ResponseFormat format = Parser.parseResponseFormat(formatStr, PARAM_FORMAT);
        boolean withDigitalInstances = queryParamToBoolean(withDigitalInstancesStr, PARAM_WITH_DIG_INST, true);
        loadUrn();
        switch (action) {
            case DECIDE:
                //pokud pochazi z katalogu, udela se redirect s tim,
                //ze se pouzije pouze DR patrici do DL registratora, ktery vlastni ten katalog
                //pokud se nenajde DR, tak se chovej jako pri SHOW
                URI instanceUriByReferer = getDigInstUriByReferer(request.getHeader(HEADER_REFERER));
                if (instanceUriByReferer != null) {
                    return redirectResponse(instanceUriByReferer);
                } else {
                    return recordResponse(format, request, withDigitalInstances);
                }
            case REDIRECT:
                URI uriByReferer = getDigInstUriByReferer(request.getHeader(HEADER_REFERER));
                if (uriByReferer != null) {
                    return redirectResponse(uriByReferer);
                } else {
                    URI anyInstanceUri = getAnyDigInstanceUri();
                    if (anyInstanceUri != null) {
                        return redirectResponse(anyInstanceUri);
                    } else {// no digital instance found
                        throw new UnknownDigitalInstanceException();
                    }
                }
            case SHOW:
                return recordResponse(format, request, withDigitalInstances);
            default:
                return recordXmlResponse(withDigitalInstances);
        }
    }

    private Response recordResponse(ResponseFormat format, HttpServletRequest request, boolean withDigitalInstances) {
        switch (format) {
            case HTML:
                return redirectResponse(webModuleUri(request));
            case XML:
                return recordXmlResponse(withDigitalInstances);
            default:
                throw new InvalidQueryParamValueException(PARAM_FORMAT, format.toString(), "");
        }
    }

    private void loadUrn() {
        if (urn == null) {
            try {
                urn = dataAccessService().urnByDigDocId(doc.getId());
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw new InternalException(ex.getMessage());
            }
        }
    }

    private Response redirectResponse(URI uri) {
        return Response.seeOther(uri).build();
    }

    private URI webModuleUri(HttpServletRequest request) {
        String serverName = request.getServerName();
        int port = request.getServerPort();
        try {
            String protocol = Configuration.WEB_REDIRECTION_SSL ? "https" : "http";
            return new URI(protocol + "://" + serverName + ":" + port + "/" + WEB_MODULE_CONTEXT + "?q=" + urn.toString());
        } catch (URISyntaxException ex) {
            throw new InternalException(ex);
        }
    }

    private Response recordXmlResponse(boolean withDigitalInstances) {
        try {
            DigitalDocumentIdentifiersBuilder digRepIdentifiersBuilder = digRepIdentifiersBuilder(doc.getId());
            List<DigitalInstanceBuilder> instancesBuilders = withDigitalInstances
                    ? instancesBuilders(doc) : null;
            RegistrarBuilder regBuilder = new RegistrarBuilder(dataAccessService().registrarById(doc.getRegistrarId()), null, null);
            ArchiverBuilder archBuilder = (doc.getRegistrarId() == doc.getArchiverId())
                    ? null : new ArchiverBuilder(dataAccessService().archiverById(doc.getArchiverId()));
            IntelectualEntityBuilder entityBuilder = entityBuilder(doc.getIntEntId());
            DigitalDocumentBuilder builder = new DigitalDocumentBuilder(doc, urn, digRepIdentifiersBuilder, instancesBuilders, regBuilder, archBuilder, entityBuilder);
            String xml = builder.buildRootElement().toXML();
            return Response.ok().entity(xml).build();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private List<DigitalInstanceBuilder> instancesBuilders(DigitalDocument rep) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().instancesByDigDocId(rep.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return result;
    }

    private IntelectualEntityBuilder entityBuilder(long intEntId) throws DatabaseException {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    @Path("/identifiers")
    public DigitalDocumentIdentifiersResource getIdentifiersResource() {
        return new DigitalDocumentIdentifiersResource(doc);
    }

    @Path("/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(doc);
    }

    private URI getDigInstUriByReferer(String refererUrl) {
        if (refererUrl == null || refererUrl.isEmpty()) {
            return null;
        }
        try {
            //vsechny digitalni instance DD
            List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
            List<Catalog> catalogs = dataAccessService().catalogs();
            //vsechny katalogy, u kterych se shoduje prefix
            List<Catalog> matching = catalogsWithMatchingReferer(catalogs, refererUrl);
            for (Catalog catalog : matching) {
                //digitalni knihovny vlastnene stejnym registratorem, jako katalog
                List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(catalog.getRegistrarId());
                for (DigitalLibrary library : libraries) {
                    //instance DD
                    for (DigitalInstance instance : instances) {
                        //instance je ve vhodne knihovne
                        if (instance.getLibraryId() == library.getId()) {
                            return toUri(instance.getUrl());
                        }
                    }
                }
            }
            return null;
        } catch (DatabaseException ex) {
            Logger.getLogger(DigitalDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private List<Catalog> catalogsWithMatchingReferer(List<Catalog> catalogs, String url) {
        if (catalogs.isEmpty()) {
            return catalogs;
        }
        List<Catalog> result = new ArrayList<Catalog>();
        for (Catalog catalog : catalogs) {
            if (matches(catalog.getUrlPrefix(), url)) {
                result.add(catalog);
            }
        }
        return result;
    }

    private boolean matches(String urlPrefix, String url) {
        return urlPrefix != null && !urlPrefix.isEmpty() && url.startsWith(urlPrefix);
    }

    private URI toUri(String uriStr) {
        try {
            return new URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new InternalException(ex);
        }
    }

    private URI getAnyDigInstanceUri() {
        try {
            List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
            if (!instances.isEmpty()) {
                return toUri(instances.get(0).getUrl());
            } else {
                return null;
            }
        } catch (DatabaseException ex) {
            Logger.getLogger(DigitalDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
