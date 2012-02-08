/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;

/**
 *
 * @author Martin Řehánek
 */
public class DataUpdateServiceImpl extends BusinessServiceImpl implements DataUpdateService {

    public DataUpdateServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    public void updateDigRepIdentifier(DigDocIdentifier id) throws UnknownRegistrarException, UnknownDigRepException, IdentifierConflictException {
        try {
            factory.digRepIdDao().updateDigRepIdValue(id);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            if (DigDocIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
                throw new UnknownDigRepException(id.getDigDocId());
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
