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
package cz.nkp.urnnbn.api.v6;

import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v6.exceptions.InternalException;
import cz.nkp.urnnbn.api.v6.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v6.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.api.v6.json.*;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.xml.apiv6.builders.*;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApiV6Resource extends Resource {

    // because http://stackoverflow.com/questions/5514087/jersey-rest-default-character-encoding/20569571
    public static final String JSON_WITH_UTF8 = "application/json;charset=utf-8";
    protected static final String PARAM_FORMAT = "format";

    protected RegistrarScopeIdentifiersBuilder registrarScopeIdentifiersBuilderXml(long digDocId) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilder> builders = new ArrayList<RegistrarScopeIdentifierBuilder>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            builders.add(new RegistrarScopeIdentifierBuilder(id));
        }
        return new RegistrarScopeIdentifiersBuilder(builders);
    }

    protected RegistrarBuilder registrarBuilderXml(Registrar registrar, boolean withDigitalLibraries, boolean withCatalogs) {
        DigitalLibrariesBuilderXml libBuilder = withDigitalLibraries ? digitalLibrariesBuilderXml(registrar) : null;
        CatalogsBuilderXml catBuilder = withCatalogs ? catalogsBuilderXml(registrar) : null;
        return new RegistrarBuilder(registrar, libBuilder, catBuilder);
    }

    protected DigitalLibrariesBuilderXml digitalLibrariesBuilderXml(Registrar registrar) {
        List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(registrar.getId());
        return new DigitalLibrariesBuilderXml(libraries);
    }

    protected CatalogsBuilderXml catalogsBuilderXml(Registrar registrar) {
        List<Catalog> catalogs = dataAccessService().catalogsByRegistrarId(registrar.getId());
        return new CatalogsBuilderXml(catalogs);
    }

    protected RegistrarScopeIdentifiersBuilderJson registrarScopeIdentifiersBuilderJson(long digDocId) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilderJson> builders = new ArrayList<RegistrarScopeIdentifierBuilderJson>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            builders.add(new RegistrarScopeIdentifierBuilderJson(id));
        }
        return new RegistrarScopeIdentifiersBuilderJson(builders);
    }

    protected RegistrarBuilderJson registrarBuilderJson(Registrar registrar, boolean withDigitalLibraries, boolean withCatalogs) {
        DigitalLibrariesBuilderJson libBuilder = withDigitalLibraries ? digitalLibrariesBuilderJson(registrar) : null;
        CatalogsBuilderJson catBuilder = withCatalogs ? catalogsBuilderJson(registrar) : null;
        return new RegistrarBuilderJson(registrar, libBuilder, catBuilder);
    }

    protected DigitalLibrariesBuilderJson digitalLibrariesBuilderJson(Registrar registrar) {
        List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(registrar.getId());
        return new DigitalLibrariesBuilderJson(libraries);
    }

    protected CatalogsBuilderJson catalogsBuilderJson(Registrar registrar) {
        List<Catalog> catalogs = dataAccessService().catalogsByRegistrarId(registrar.getId());
        return new CatalogsBuilderJson(catalogs);
    }

    protected Document validDocumentFromString(ResponseFormat format, String content, String schema) {
        try {
            return XOMUtils.loadDocumentValidByExternalXsd(content, schema);
        } catch (ValidityException ex) {
            throw new InvalidDataException(format, ex.getMessage());
        } catch (ParsingException ex) {
            throw new InvalidDataException(format, ex.getMessage());
        } catch (IOException ex) {
            throw new InternalException(format, ex);
        }
    }

    protected final void checkServerNotReadOnly(ResponseFormat format) {
        if (ApiModuleConfiguration.instanceOf().isServerReadOnly()) {
            throw new MethodForbiddenException(format);
        }
    }

    protected URI buildWebSearchUri(String query) throws URISyntaxException {
        return new URI(ApiModuleConfiguration.instanceOf().getWebSearchUrlPrefix() + query);
    }

}
