/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import cz.nkp.urnnbn.services.exceptions.DigRepIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public interface DataImportService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataImportService.class.getName());

    /**
     * Creates new intelectual entity (or chooses another present entity if 
     * defined identifiers match) along with new digital instance. 
     * @param data
     * @param userId id of user performing this operation
     * @return UrnNbn that has been assigned or the one present in data if it can be used
     * @throws AccessException
     * @throws UrnNotFromRegistrarException
     * @throws UrnUsedException
     * @throws ImportFailedException 
     */
    public UrnNbn importNewRecord(RecordImport data, long userId) throws
            AccessException, UrnNotFromRegistrarException,
            UrnUsedException, UnknownRegistrarException,
            DigRepIdentifierCollisionException, UnknownArchiverException;

    /**
     * Creates new digital instance for existing digital representation.
     * @param instance
     * @param userId Id of user performing this operation
     * @return digital instance object with id set
     */
    public DigitalInstance addDigitalInstance(DigitalInstance instance, long userId) throws
            DatabaseException,
            AccessException,
            ImportFailedException;

    //TODO
    public void updateDigitalRepresentation(DigitalRepresentation rep,
            List<DigRepIdentifier> ids);
    //TODO: manipulace s instancema
}
