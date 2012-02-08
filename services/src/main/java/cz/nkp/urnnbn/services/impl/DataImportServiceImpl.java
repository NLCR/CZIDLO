/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.DigRepIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigiLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;

/**
 * TODO: test
 * @author Martin Řehánek
 */
public class DataImportServiceImpl extends BusinessServiceImpl implements DataImportService {

    //TODO: autentizaci a autorizaci by si mel zajistovat klient, ne jina sluzba
    private final AuthorizationModule authorization;

    public DataImportServiceImpl(DatabaseConnector conn) {
        super(conn);
        authorization = new AuthorizationModule(factory);
    }

    public UrnNbn importNewRecord(RecordImport data, long userId) throws AccessException, UrnNotFromRegistrarException, UrnUsedException, UnknownRegistrarException, DigRepIdentifierCollisionException, UnknownArchiverException {
        authorization.checkAccessRights(data.getRegistrarCode(), userId);
        return new RecordImporter(factory, data, userId).run();
    }

    public DigitalInstance addDigitalInstance(DigitalInstance instance, long userId) throws AccessException, UnknownDigiLibException, UnknownDigDocException {
        try {
            long registrarId = registrarOfDigLibrary(instance.getLibraryId());
            authorization.checkAccessRights(registrarId, userId);
            return new DigitalInstanceAdder(factory, instance).run();
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addNewDigRepId(DigDocIdentifier id) throws UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException {
        try {
            factory.digRepIdDao().insertDigDocId(id);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            if (DigDocIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigDocException(id.getDigDocId());
            } else if (RegistrarDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownRegistrarException(id.getRegistrarId());
            } else {
                throw new RuntimeException(ex);
            }
        } catch (AlreadyPresentException ex) {
            throw new IdentifierConflictException(id.getType().toString(), id.getValue());
        }
    }
}
