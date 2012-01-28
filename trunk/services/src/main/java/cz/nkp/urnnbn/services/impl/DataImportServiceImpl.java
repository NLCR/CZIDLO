/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import cz.nkp.urnnbn.services.exceptions.DigRepIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import java.util.List;

/**
 * TODO: test
 * @author Martin Řehánek
 */
public class DataImportServiceImpl extends BusinessServiceImpl implements DataImportService {

    private final AuthorizationModule authorization;

    public DataImportServiceImpl(DatabaseConnector conn) {
        super(conn);
        authorization = new AuthorizationModule(factory);
    }

    public UrnNbn importNewRecord(RecordImport data, long userId) throws AccessException, UrnNotFromRegistrarException, UrnUsedException, UnknownRegistrarException, DigRepIdentifierCollisionException {
        authorization.checkAccessRights(data.getRegistrarSigla(), userId);
        return new RecordImporter(factory, data, userId).run();
    }

    public DigitalInstance addDigitalInstance(DigitalInstance instance, long userId) throws DatabaseException, AccessException, ImportFailedException {
        try {
            long registrarId = registrarOfDigLibrary(instance.getLibraryId());
            authorization.checkAccessRights(registrarId, userId);
            return new DigitalInstanceAdder(factory, instance).run();
        } catch (RecordNotFoundException ex) {
            throw new ImportFailedException(ex);
        }
    }

    public void updateDigitalRepresentation(DigitalRepresentation rep, List<DigRepIdentifier> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
