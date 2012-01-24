/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
class DigitalInstanceAdder extends BusinessServiceImpl {

    private static final Logger logger = Logger.getLogger(DataImportService.class.getName());
    private final DigitalInstance instance;

    DigitalInstanceAdder(DAOFactory factory, DigitalInstance instance) {
        super(factory);
        this.instance = instance;
    }

    DigitalInstance run() throws DatabaseException, ImportFailedException {
        try {
            //long registrarId = registrarOfDigLibrary(instance.getDigRepId());
            //to je spatne. registrator muze byt jakykoliv
            //checkLibraryBelongsToRegistrar(registrarId, instance.getDigRepId());
            Long id = factory.digInstDao().insertDigInstance(instance);
            instance.setId(id);
            return instance;
        } catch (RecordNotFoundException ex) {
            throw new ImportFailedException(ex);
        }
    }
}
