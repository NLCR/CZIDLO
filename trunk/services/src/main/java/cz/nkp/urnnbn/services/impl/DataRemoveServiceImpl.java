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
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;

/**
 *
 * @author Martin Řehánek
 */
public class DataRemoveServiceImpl extends BusinessServiceImpl implements DataRemoveService {

    public DataRemoveServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    public void removeDigitalRepresentationIdentifiers(long digRepId) throws UnknownDigRepException {
        try {
            factory.digRepIdDao().deleteAllIdentifiersOfDigDoc(digRepId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigRepException(digRepId);
        }
    }

    public void removeDigitalRepresentationId(long digRepId, DigDocIdType type) throws UnknownDigRepException, DigRepIdNotDefinedException {
        try {
            factory.digRepIdDao().deleteDigDocIdentifier(digRepId, type);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            if (DigitalDocumentDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigRepException(digRepId);
            } else if (DigDocIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new DigRepIdNotDefinedException(type);
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
}
