/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.BusinessService;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
abstract class BusinessServiceImpl implements BusinessService {

    private static final Logger logger = Logger.getLogger(BusinessServiceImpl.class.getName());
    final DAOFactory factory;
    final AuthorizationModule authorization;

    public BusinessServiceImpl(DatabaseConnector conn) {
        this(new DAOFactory(conn));
    }

    public BusinessServiceImpl(DAOFactory factory) {
        this.factory = factory;
        authorization = new AuthorizationModule(factory);
    }

    long registrarOfDigLibrary(long libraryId) throws DatabaseException, UnknownDigLibException {
        try {
            DigitalLibrary lib = factory.diglLibDao().getLibraryById(libraryId);
            return lib.getRegistrarId();
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigLibException(libraryId);
        }
    }

    long registrarOfCatalog(long catalogId) throws DatabaseException, UnknownCatalogException {
        try {
            Catalog catalog = factory.catalogDao().getCatalogById(catalogId);
            return catalog.getRegistrarId();
        } catch (RecordNotFoundException ex) {
            throw new UnknownCatalogException(catalogId);
        }
    }

    long registrarOfDigInstance(long digInstanceId) throws DatabaseException, UnknownDigInstException {
        try {
            DigitalInstance instance = factory.digInstDao().getDigInstanceById(digInstanceId);
            DigitalLibrary library = factory.diglLibDao().getLibraryById(instance.getLibraryId());
            return library.getRegistrarId();
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigInstException(digInstanceId);
        }
    }
}
