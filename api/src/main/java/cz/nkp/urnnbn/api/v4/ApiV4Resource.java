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

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.xml.apiv4.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifiersBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;

public abstract class ApiV4Resource extends Resource {

    protected static final String PARAM_FORMAT = "format";

    protected RegistrarScopeIdentifiersBuilder registrarScopeIdentifiersBuilder(long digDocId) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilder> builders = new ArrayList<RegistrarScopeIdentifierBuilder>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            builders.add(new RegistrarScopeIdentifierBuilder(id));
        }
        return new RegistrarScopeIdentifiersBuilder(builders);
    }

    protected RegistrarBuilder registrarBuilder(Registrar registrar, boolean withDigitalLibraries, boolean withCatalogs) {
        DigitalLibrariesBuilder libBuilder = withDigitalLibraries ? librariesBuilder(registrar) : null;
        CatalogsBuilder catBuilder = withCatalogs ? catalogsBuilder(registrar) : null;
        return new RegistrarBuilder(registrar, libBuilder, catBuilder);
    }

    protected DigitalLibrariesBuilder librariesBuilder(Registrar registrar) {
        List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(registrar.getId());
        return new DigitalLibrariesBuilder(libraries);
    }

    protected CatalogsBuilder catalogsBuilder(Registrar registrar) {
        List<Catalog> catalogs = dataAccessService().catalogsByRegistrarId(registrar.getId());
        return new CatalogsBuilder(catalogs);
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
