/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigiLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;

/**
 *
 * @author Martin Řehánek
 */
class DigitalInstanceAdder extends BusinessServiceImpl {

    private final DigitalInstance instance;

    DigitalInstanceAdder(DAOFactory factory, DigitalInstance instance) {
        super(factory);
        this.instance = instance;
    }

    DigitalInstance run() throws DatabaseException, UnknownDigiLibException, UnknownDigDocException {
        try {
            Long instanceId = factory.digInstDao().insertDigInstance(instance);
            instance.setId(instanceId);
            return instance;
        } catch (RecordNotFoundException ex) {
            if (DigitalLibraryDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigiLibException(instance.getLibraryId());
            } else if (DigitalDocumentDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigDocException(instance.getDigDocId());
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
}
