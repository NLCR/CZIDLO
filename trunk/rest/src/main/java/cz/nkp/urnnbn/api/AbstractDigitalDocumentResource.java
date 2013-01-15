/*
 * Copyright (C) 2013 Martin Řehánek
 *
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
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.api.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v3.DigitalDocumentResource;
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
import cz.nkp.urnnbn.xml.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class AbstractDigitalDocumentResource extends Resource {

    protected static final String PARAM_FORMAT = "format";
    private static final String WEB_MODULE_CONTEXT = "web";
    private static final String HEADER_REFERER = "referer";
    protected final DigitalDocument doc;
    protected UrnNbn urn;

    public AbstractDigitalDocumentResource(DigitalDocument doc, UrnNbn urn) {
        this.doc = doc;
        this.urn = urn;
    }

    public Response resolve(Action action, ResponseFormat format, HttpServletRequest request, boolean withDigitalInstances) {
        switch (action) {
            case DECIDE:
                //pokud se najde vhodna digitalni instance, je presmerovano
                //preferuje se dig. inst. z nektere dig. knihovny registratora, kteremu patri katalog, jehoz prefix se shoduje s refererem
                //jinak se pouzije jakakoliv jina (aktivni) digitalni instance
                //pokud neni digitalni instance nalezena, zobrazi se zaznam DD
                URI digitalInstance = getDigInstUriOrNull(request.getHeader(HEADER_REFERER));
                if (digitalInstance != null) {
                    return redirectResponse(digitalInstance);
                } else {
                    return recordResponse(format, request, withDigitalInstances);
                }
            case REDIRECT:
                URI uriByReferer = getDigInstUriOrNull(request.getHeader(HEADER_REFERER));
                if (uriByReferer != null) {
                    return redirectResponse(uriByReferer);
                } else {
                    URI anyInstanceUri = getAnyActiveDigInstanceUri();
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

    private URI getAnyActiveDigInstanceUri() {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        for (DigitalInstance instance : instances) {
            if (instance.isActive()) {
                return toUri(instance.getUrl());
            }
        }
        return null;
    }

    private URI getDigInstUriOrNull(String refererUrl) {
        try {
            List<DigitalInstance> allDigitalInstanceds = dataAccessService().digInstancesByDigDocId(doc.getId());
            DigitalInstance instanceByReferer = digInstanceByReferer(allDigitalInstanceds, refererUrl);
            if (instanceByReferer != null) { //prefered uri found
                return toUri(instanceByReferer.getUrl());
            } else { //return any uri
                for (DigitalInstance instance : allDigitalInstanceds) {
                    if (instance.isActive()) {
                        toUri(instance.getUrl());
                    }
                }
            }
            return null;
        } catch (DatabaseException ex) {
            Logger.getLogger(DigitalDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private DigitalInstance digInstanceByReferer(List<DigitalInstance> allInstances, String refererUrl) throws DatabaseException {
        if (refererUrl == null || refererUrl.isEmpty()) {
            return null;
        }
        //vsechny katalogy 
        List<Catalog> catalogs = dataAccessService().catalogs();
        //vsechny katalogy, u kterych se shoduje prefix
        List<Catalog> matching = catalogsWithMatchingReferer(catalogs, refererUrl);
        for (Catalog catalog : matching) {
            //digitalni knihovny vlastnene stejnym registratorem, jako katalog
            List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(catalog.getRegistrarId());
            for (DigitalLibrary library : libraries) {
                //instance DD
                for (DigitalInstance instance : allInstances) {
                    //instance je ve vhodne knihovne a je aktivni
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

    private URI toUri(String uriStr) {
        try {
            return new URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new InternalException(ex);
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

    private Response redirectResponse(URI uri) {
        return Response.seeOther(uri).build();
    }

    private URI webModuleUri(HttpServletRequest request) {
        String serverName = request.getServerName();
        int port = request.getServerPort();
        try {
            return new URI("http://" + serverName + ":" + port + "/" + WEB_MODULE_CONTEXT + "?q=" + urn.toString());
        } catch (URISyntaxException ex) {
            throw new InternalException(ex);
        }
    }

    private Response recordXmlResponse(boolean withDigitalInstances) {
        try {
            RegistrarScopeIdentifiersBuilder digRepIdentifiersBuilder = registrarScopeIdentifiersBuilder(doc.getId());
            DigitalInstancesBuilder instancesBuilder = withDigitalInstances
                    ? instancesBuilder(doc) : null;
            RegistrarBuilder regBuilder = new RegistrarBuilder(dataAccessService().registrarById(doc.getRegistrarId()), null, null);
            ArchiverBuilder archBuilder = (doc.getRegistrarId() == doc.getArchiverId())
                    ? null : new ArchiverBuilder(dataAccessService().archiverById(doc.getArchiverId()));
            IntelectualEntityBuilder entityBuilder = entityBuilder(doc.getIntEntId());
            DigitalDocumentBuilder builder = new DigitalDocumentBuilder(doc, urn, digRepIdentifiersBuilder, instancesBuilder, regBuilder, archBuilder, entityBuilder);
            String xml = builder.buildDocumentWithResponseHeader().toXML();
            return Response.ok().entity(xml).build();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalInstancesBuilder instancesBuilder(DigitalDocument doc) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilder(result);
    }

    private IntelectualEntityBuilder entityBuilder(long intEntId) throws DatabaseException {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    protected void loadUrn() {
        if (urn == null) {
            urn = dataAccessService().urnByDigDocId(doc.getId(), true);
        }
    }
}
