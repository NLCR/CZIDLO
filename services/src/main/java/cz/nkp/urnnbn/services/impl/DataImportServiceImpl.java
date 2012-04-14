/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.Registrar;
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
import cz.nkp.urnnbn.services.exceptions.DigDocIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistrarCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigiLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
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

    @Override
    public UrnNbn importNewRecord(RecordImport data, String login) throws AccessException, UrnNotFromRegistrarException, UrnUsedException, UnknownRegistrarException, DigDocIdentifierCollisionException, UnknownArchiverException, UnknownUserException {
        authorization.checkAccessRights(data.getRegistrarCode(), login);
        return new RecordImporter(factory, data).run();
    }

    @Override
    public DigitalInstance addDigitalInstance(DigitalInstance instance, String login) throws AccessException, UnknownDigiLibException, UnknownDigDocException, UnknownUserException {
        try {
            long registrarId = registrarOfDigLibrary(instance.getLibraryId());
            authorization.checkAccessRights(registrarId, login);
            return new DigitalInstanceAdder(factory, instance).run();
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addRegistrarScopeIdentifier(DigDocIdentifier id, String login) throws UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException, AccessException, UnknownUserException {
        try {
            authorization.checkAccessRights(id.getRegistrarId(), login);
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

    @Override
    public Archiver insertNewArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException {
        try {
            authorization.checkAdminRights(login);
            Long id = factory.archiverDao().insertArchiver(archiver);
            archiver.setId(id);
            return archiver;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Registrar insertNewRegistrar(Registrar registrar, String login) throws UnknownUserException, NotAdminException, RegistrarCollisionException {
        try {
            authorization.checkAdminRights(login);
            Long id = factory.registrarDao().insertRegistrar(registrar);
            registrar.setId(id);
            return registrar;
        } catch (AlreadyPresentException ex) {
            throw new RegistrarCollisionException(registrar.getCode().toString());
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
