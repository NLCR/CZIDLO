/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.BusinessService;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
abstract class BusinessServiceImpl implements BusinessService {

    private static final Logger logger = Logger.getLogger(BusinessServiceImpl.class.getName());
    DAOFactory factory;

    public BusinessServiceImpl(DatabaseConnector conn) {
        factory = new DAOFactory(conn);
    }

    public BusinessServiceImpl(DAOFactory factory) {
        this.factory = factory;
    }

    long registrarOfDigLibrary(long digRepId) throws DatabaseException, RecordNotFoundException {
        DigitalLibrary lib = factory.digitalLibraryDao().getLibraryById(digRepId);
        return lib.getRegistrarId();
    }
}
