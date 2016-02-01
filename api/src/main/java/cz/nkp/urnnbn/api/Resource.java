/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v3.exceptions.MethodForbiddenException;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.StatisticService;
import cz.nkp.urnnbn.services.UrnNbnReservationService;
import cz.nkp.urnnbn.xml.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import cz.nkp.urnnbn.xml.commons.ValidatingXmlLoader;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public class Resource {

    protected static final Logger logger = Logger.getLogger(Resource.class.getName());

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected UrnNbnReservationService urnReservationService() {
        return Services.instanceOf().urnReservationService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }
    
    protected StatisticService statisticService(){
        return Services.instanceOf().statisticService();
    }

    protected RegistrarScopeIdentifiersBuilder registrarScopeIdentifiersBuilder(long digDocId) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilder> builders = new ArrayList<RegistrarScopeIdentifierBuilder>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            builders.add(new RegistrarScopeIdentifierBuilder(id));
        }
        return new RegistrarScopeIdentifiersBuilder(builders);
    }

    protected RegistrarBuilder registrarBuilder(Registrar registrar, boolean withDigitalLibraries, boolean withCatalogs) {
        DigitalLibrariesBuilder libBuilder = withDigitalLibraries
                ? librariesBuilder(registrar) : null;
        CatalogsBuilder catBuilder = withCatalogs
                ? catalogsBuilder(registrar) : null;
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
