/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDataException;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.UrnNbnReservationService;
import cz.nkp.urnnbn.xml.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
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

    static final Logger logger = Logger.getLogger(Resource.class.getName());

    DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    UrnNbnReservationService urnReservationService() {
        return Services.instanceOf().urnReservationService();
    }

    DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    RegistrarScopeIdentifiersBuilder registrarScopeIdentifiersBuilder(long digDocId) throws DatabaseException {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilder> builders = new ArrayList<RegistrarScopeIdentifierBuilder>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            builders.add(new RegistrarScopeIdentifierBuilder(id));
        }
        return new RegistrarScopeIdentifiersBuilder(builders);
    }

    RegistrarBuilder registrarBuilder(Registrar registrar, boolean withDigitalLibraries, boolean withCatalogs) throws DatabaseException {
        DigitalLibrariesBuilder libBuilder = withDigitalLibraries
                ? librariesBuilder(registrar) : null;
        CatalogsBuilder catBuilder = withCatalogs
                ? catalogsBuilder(registrar) : null;
        return new RegistrarBuilder(registrar, libBuilder, catBuilder);
    }

    DigitalLibrariesBuilder librariesBuilder(Registrar registrar) throws DatabaseException {
        List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(registrar.getId());
        return new DigitalLibrariesBuilder(libraries);
    }

    CatalogsBuilder catalogsBuilder(Registrar registrar) throws DatabaseException {
        List<Catalog> catalogs = dataAccessService().catalogsByRegistrarId(registrar.getId());
        return new CatalogsBuilder(catalogs);
    }

    Document validDocumentFromString(String content, String schema) {
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
}
