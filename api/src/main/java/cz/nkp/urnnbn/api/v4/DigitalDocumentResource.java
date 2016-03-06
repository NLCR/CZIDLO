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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v4.exceptions.UrnNbnDeactivated;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.apiv4.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;

public class DigitalDocumentResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentResource.class.getName());
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_WITH_DIG_INST = "digitalInstances";
    private static final String WEB_MODULE_CONTEXT = "web";
    private static final String HEADER_REFERER = "referer";
    private final DigitalDocument doc;
    private final UrnNbn urn;

    public DigitalDocumentResource(DigitalDocument doc, UrnNbn urn) {
        this.doc = doc;
        this.urn = urn;
    }

    @Path("/registrarScopeIdentifiers")
    public RegistrarScopeIdentifiersResource getRegistrarScopeIdentifiersResource() {
        return new RegistrarScopeIdentifiersResource(doc);
    }

    @Path("/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(doc);
    }

    @GET
    @Produces("application/xml")
    public Response resolve(@DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_WITH_DIG_INST) String withDigitalInstancesStr, @Context HttpServletRequest request) {
        ResponseFormat format2 = ResponseFormat.XML;// TODO: parse format, support xml and json
        try {
            Action action = Parser.parseAction(format2, actionStr, PARAM_ACTION);
            ResponseFormat format = Parser.parseResponseFormat(format2, formatStr, PARAM_FORMAT);
            boolean withDigitalInstances = true;
            if (withDigitalInstancesStr != null) {
                withDigitalInstances = Parser.parseBooleanQueryParam(format2, withDigitalInstancesStr, PARAM_WITH_DIG_INST);
            }
            return resolve(action, format, request, withDigitalInstances);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(format2, e);
        }
    }

    public Response resolve(Action action, ResponseFormat format, HttpServletRequest request, boolean withDigitalInstances) {
        // for deactivated URN:NBNs the redirection is impossible
        if (!urn.isActive()) { // error if force to redirect
            if (action == Action.REDIRECT) {
                throw new UrnNbnDeactivated(format, urn);
            } else if (action == Action.DECIDE) { // otherwise it is decided to show the recored
                action = Action.SHOW;
            }
        }
        switch (action) {
        case DECIDE:
            // pokud se najde vhodna digitalni instance, je presmerovano
            // preferuje se dig. inst. z nektere dig. knihovny registratora, kteremu patri katalog, jehoz prefix se shoduje s refererem
            // jinak se pouzije jakakoliv jina (aktivni) digitalni instance
            // pokud neni digitalni instance nalezena, zobrazi se zaznam DD
            URI digitalInstance = getDigInstUriOrNull(format, request.getHeader(HEADER_REFERER));
            if (digitalInstance != null) {
                // redirect to digital instance
                return Response.seeOther(digitalInstance).build();
            } else {
                return showRecordResponse(format, request, withDigitalInstances);
            }
        case REDIRECT:
            URI uriByReferer = getDigInstUriOrNull(format, request.getHeader(HEADER_REFERER));
            if (uriByReferer != null) {
                return Response.seeOther(uriByReferer).build();
            } else {
                URI anyInstanceUri = getAnyActiveDigInstanceUri(format);
                if (anyInstanceUri != null) {
                    return Response.seeOther(anyInstanceUri).build();
                } else {// no digital instance found
                    throw new UnknownDigitalInstanceException(format);
                }
            }
        case SHOW:
            return showRecordResponse(format, request, withDigitalInstances);
        default:
            return buildDigitalDocumentRecordXml(withDigitalInstances);
        }
    }

    private URI getAnyActiveDigInstanceUri(ResponseFormat format) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        for (DigitalInstance instance : instances) {
            if (instance.isActive()) {
                return toUri(format, instance.getUrl());
            }
        }
        return null;
    }

    private URI getDigInstUriOrNull(ResponseFormat format, String refererUrl) {
        try {
            List<DigitalInstance> allDigitalInstanceds = dataAccessService().digInstancesByDigDocId(doc.getId());
            DigitalInstance instanceByReferer = digInstanceByReferer(allDigitalInstanceds, refererUrl);
            if (instanceByReferer != null) { // prefered uri found
                return toUri(format, instanceByReferer.getUrl());
            } else { // return any uri
                for (DigitalInstance instance : allDigitalInstanceds) {
                    if (instance.isActive()) {
                        return toUri(format, instance.getUrl());
                    }
                }
            }
            return null;
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private DigitalInstance digInstanceByReferer(List<DigitalInstance> allInstances, String refererUrl) throws DatabaseException {
        if (refererUrl == null || refererUrl.isEmpty()) {
            return null;
        }
        // vsechny katalogy
        List<Catalog> catalogs = dataAccessService().catalogs();
        // vsechny katalogy, u kterych se shoduje prefix
        List<Catalog> matching = catalogsWithMatchingReferer(catalogs, refererUrl);
        for (Catalog catalog : matching) {
            // digitalni knihovny vlastnene stejnym registratorem, jako katalog
            List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(catalog.getRegistrarId());
            for (DigitalLibrary library : libraries) {
                // instance DD
                for (DigitalInstance instance : allInstances) {
                    // instance je ve vhodne knihovne a je aktivni
                    if (instance.getLibraryId() == library.getId() && instance.isActive()) {
                        return instance;
                    }
                }
            }
        }
        return null;
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

    private URI toUri(ResponseFormat format, String uriStr) {
        try {
            return new URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new InternalException(format, ex);
        }
    }

    private Response showRecordResponse(ResponseFormat format, HttpServletRequest request, boolean withDigitalInstances) {
        switch (format) {
        case HTML:
            return Response.seeOther(webModuleUri(format, request)).build();
        case XML:
            return buildDigitalDocumentRecordXml(withDigitalInstances);
        default:
            throw new InvalidQueryParamValueException(format, PARAM_FORMAT, format.toString(), "");
        }
    }

    /**
     * Redirects to web interface. It is allways redirected to http://SERVER_NAME/WEB_MODULE_CONTEXT?q=URN where SERVER_NAME is same as the one in
     * request, WEB_MODULE_CONTEXT is "web" in default. Web server should perform redirection to HTTPS if desirable.
     *
     * @param request
     * @return
     */
    private URI webModuleUri(ResponseFormat format, HttpServletRequest request) {
        try {
            return new URI("http://" + request.getServerName() + ":" + request.getServerPort() + "/" + WEB_MODULE_CONTEXT + "?q=" + urn.toString());
        } catch (URISyntaxException ex) {
            throw new InternalException(format, ex);
        }
    }

    private Response buildDigitalDocumentRecordXml(boolean withDigitalInstances) {
        DigitalDocumentBuilder builder = digitalDocumentBuilder(withDigitalInstances);
        String xml = builder.buildDocumentWithResponseHeader().toXML();
        return Response.ok().entity(xml).build();
    }

    private DigitalDocumentBuilder digitalDocumentBuilder(boolean withDigitalInstances) {
        return new DigitalDocumentBuilder(doc, urn, registrarScopeIdentifiersBuilder(doc.getId()), withDigitalInstances ? instancesBuilder() : null,
                registrarBuilder(), archiverBuilder(), entityBuilder());
    }

    private DigitalInstancesBuilder instancesBuilder() {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilder(result);
    }

    private RegistrarBuilder registrarBuilder() {
        return new RegistrarBuilder(dataAccessService().registrarById(doc.getRegistrarId()), null, null);
    }

    private ArchiverBuilder archiverBuilder() {
        if (doc.getRegistrarId() == doc.getArchiverId()) {
            return null;
        } else {
            return new ArchiverBuilder(dataAccessService().archiverById(doc.getArchiverId()));
        }
    }

    private IntelectualEntityBuilder entityBuilder() {
        long intEntId = doc.getIntEntId();
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }
}
