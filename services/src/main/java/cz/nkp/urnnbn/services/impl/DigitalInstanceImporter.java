/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;

/**
 *
 * @author Martin Řehánek
 */
class DigitalInstanceImporter extends BusinessServiceImpl {

    private static final Logger logger = Logger.getLogger(DigitalInstanceImporter.class.getName());
    private final DigitalInstance instance;

    /**
     * 
     * @param factory
     * @param instance
     *            Digital instance created by api/web operation without it's id asssigned yet.
     */
    DigitalInstanceImporter(DAOFactory factory, DigitalInstance instance) {
        super(factory);
        this.instance = instance;
    }

    /**
     * 
     * @return Digital instance with generated id, timestampes, etc.
     * @throws DatabaseException
     * @throws UnknownDigLibException
     * @throws UnknownDigDocException
     */
    DigitalInstance run() throws DatabaseException, UnknownDigLibException, UnknownDigDocException {
        try {
            Long instanceId = factory.digInstDao().insertDigInstance(instance);
            DigitalInstance result = factory.digInstDao().getDigInstanceById(instanceId);
            logger.log(Level.INFO, "{0} was inserted", result);
            return result;
        } catch (RecordNotFoundException ex) {
            if (DigitalLibraryDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigLibException(instance.getLibraryId());
            } else if (DigitalDocumentDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigDocException(instance.getDigDocId());
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
}
