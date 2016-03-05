package cz.nkp.urnnbn.api.v2_v3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import cz.nkp.urnnbn.api.Resource;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v3.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.xml.apiv3.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarScopeIdentifiersBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

public class ApiV2V3Resource extends Resource {

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

    protected Document validDocumentFromString(String content, String schema) {
        try {
            return XOMUtils.loadDocumentValidByExternalXsd(content, schema);
        } catch (ValidityException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidDataException(ex.getMessage());
        } catch (ParsingException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidDataException(ex.getMessage());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        }
    }

    protected String transformApiV3ToApiV2ResponseAsString(XsltXmlTransformer transformer, String xml) throws InternalException {
        return transformApiV3ToApiV2ResponseAsDocument(transformer, xml).toXML();
    }

    protected Document transformApiV3ToApiV2ResponseAsDocument(XsltXmlTransformer transformer, String xml) throws InternalException {
        try {
            return transformer.transform(xml);
        } catch (Throwable ex) {
            throw new InternalException(ex);
        }
    }

    protected final void checkServerNotReadOnly() {
        if (ApiModuleConfiguration.instanceOf().isServerReadOnly()) {
            throw new MethodForbiddenException();
        }
    }

}
