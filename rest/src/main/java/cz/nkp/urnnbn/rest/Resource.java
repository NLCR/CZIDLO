/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.rest.config.Configuration;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDataException;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.UrnNbnReservationService;
import cz.nkp.urnnbn.services.impl.DataAccessServiceImpl;
import cz.nkp.urnnbn.services.impl.DataImportServiceImpl;
import cz.nkp.urnnbn.services.impl.DataRemoveServiceImpl;
import cz.nkp.urnnbn.services.impl.DataUpdateServiceImpl;
import cz.nkp.urnnbn.services.impl.UrnNbnReservationServiceImpl;
import cz.nkp.urnnbn.xml.builders.CatalogsBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibrariesBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public class Resource {

    private static DatabaseConnector connector = DatabaseConnectorFactory.getConnector();
    static final Logger logger = Logger.getLogger("rest api v2");
    private static DataAccessService dataAccess;
    private static DataImportService dataImport;
    private static DataRemoveService dataRemove;
    private static DataUpdateService dataUpdate;
    private static UrnNbnReservationService urnReservation;

    DataAccessService dataAccessService() {
        if (dataAccess == null) {
            dataAccess = new DataAccessServiceImpl(connector);
        }
        return dataAccess;
    }

    DataImportService dataImportService() {
        if (dataImport == null) {
            dataImport = new DataImportServiceImpl(connector);
        }
        return dataImport;
    }

    UrnNbnReservationService urnReservationService() {
        if (urnReservation == null) {
            urnReservation = new UrnNbnReservationServiceImpl(connector, Configuration.URN_RESERVATION_MAX_SIZE);
        }
        return urnReservation;
    }

    DataRemoveService dataRemoveService() {
        if (dataRemove == null) {
            dataRemove = new DataRemoveServiceImpl(connector);
        }
        return dataRemove;
    }

    DataUpdateService dataUpdate() {
        if (dataUpdate == null) {
            dataUpdate = new DataUpdateServiceImpl(connector);
        }
        return dataUpdate;
    }

    DigitalDocumentIdentifiersBuilder digRepIdentifiersBuilder(long digRepId) throws DatabaseException {
        List<DigDocIdentifier> identifiers = dataAccessService().digDocIdentifiersByDigDocId(digRepId);
        List<DigitalDocumentIdentifierBuilder> builders = new ArrayList<DigitalDocumentIdentifierBuilder>(identifiers.size());
        for (DigDocIdentifier id : identifiers) {
            builders.add(new DigitalDocumentIdentifierBuilder(id));
        }
        return new DigitalDocumentIdentifiersBuilder(builders);
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

    boolean queryParamToBoolean(String stringValue, String paramName, boolean defaultValue) {
        return "".equals(stringValue) ? defaultValue
                : Parser.parseBooleanQueryParam(stringValue, paramName);
    }
}
