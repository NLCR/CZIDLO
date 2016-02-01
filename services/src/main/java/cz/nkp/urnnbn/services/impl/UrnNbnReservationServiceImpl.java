/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.UrnNbnReservationService;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservationServiceImpl extends BusinessServiceImpl implements UrnNbnReservationService {

    private static final Logger logger = Logger.getLogger(UrnNbnReservationService.class.getName());
    private final int maxBatchSize;

    public UrnNbnReservationServiceImpl(DatabaseConnector conn, int maxBatchSize) {
        super(conn);
        if (maxBatchSize <= 0) {
            throw new IllegalArgumentException("maxBatchSize must be positive number");
        }
        this.maxBatchSize = maxBatchSize;
    }

    @Override
    public List<UrnNbn> reserveUrnNbnBatch(int batchSize, Registrar registrar, String login) throws UnknownUserException, AccessException {
        try {
            authorization.checkAccessRights(registrar.getId(), login);
            List<UrnNbn> result = new ArrayList<UrnNbn>(batchSize);
            for (int i = 0; i < batchSize; i++) {
                UrnNbn found = findAndSaveNewUrnNbn(registrar);
                if (found != null) {
                    result.add(found);
                }
            }
            return result;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<UrnNbn> getReservedUrnNbnList(long registrarId) throws UnknownRegistrarException {
        try {
            return factory.urnReservedDao().getUrnNbnList(registrarId);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    @Override
    public boolean isReserved(UrnNbn urn) {
        try {
            System.err.println("isReserved: " + urn);
            factory.urnReservedDao().getUrn(urn.getRegistrarCode(), urn.getDocumentCode());
            // when RecordNotFound is not thrown the urn:nbn is reserved
            return true;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            System.err.println("NOT FOUND in RESERVED_URN_TABLE");
            return false;
        }
    }

    // POZOR: nekdy vraci null. jeste rozmyslet, jak se tady zachovat
    // nejspis nakonec budou orm vyjimky nehlidane
    private UrnNbn findAndSaveNewUrnNbn(Registrar registrar) throws DatabaseException {
        UrnNbnFinder finder = new UrnNbnFinder(factory, registrar);
        UrnNbn found = finder.findNewUrnNbn();
        try {
            factory.urnReservedDao().insertUrnNbn(found, registrar.getId());
            return found;
        } catch (AlreadyPresentException ex) {
            // should never happend
            logger.log(Level.SEVERE, null, ex);
            return null;
        } catch (RecordNotFoundException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
