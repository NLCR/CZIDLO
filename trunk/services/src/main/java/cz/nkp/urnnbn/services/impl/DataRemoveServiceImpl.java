/*F
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public class DataRemoveServiceImpl extends BusinessServiceImpl implements DataRemoveService {

    public DataRemoveServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    @Override
    public void removeDigitalDocumentIdentifiers(long digRepId) throws UnknownDigDocException {
        try {
            factory.digRepIdDao().deleteAllIdentifiersOfDigDoc(digRepId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigDocException(digRepId);
        }
    }

    @Override
    public void removeDigitalDocumentId(long digRepId, DigDocIdType type) throws UnknownDigDocException, DigRepIdNotDefinedException {
        try {
            factory.digRepIdDao().deleteDigDocIdentifier(digRepId, type);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            if (DigitalDocumentDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigDocException(digRepId);
            } else if (DigDocIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new DigRepIdNotDefinedException(type);
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void removeArchiver(long archiverId, String login) throws UnknownArchiverException, CannotBeRemovedException, NotAdminException, UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            factory.archiverDao().deleteArchiver(archiverId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownArchiverException(archiverId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }

    @Override
    public void removeRegistrar(long registrarId, String login) throws UnknownRegistrarException, CannotBeRemovedException, NotAdminException, UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            factory.registrarDao().deleteRegistrar(registrarId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }
}
