/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnReservationService extends BusinessService {

    /**
     * This operation reserves certain number of urnNbns. UrnNbns that are reservec
     * are not used for assigning by the application. The registrar is expected 
     * to put those urnNbns into it's metadata records that are ready for import
     * into the Resolver application. When one of those records is imported, 
     * the application assignes urnNbn, that is present in import metadata, 
     * to newly created  digital instance.
     * @param batchSize number of urnNbn identifiers returned
     * @param registrar
     * @param userId Id of user to perform this operation.
     * @return Batch of urnNbn of size batchSize.
     */
    public List<UrnNbn> reserveUrnNbnBatch(
            int batchSize,
            Registrar registrar,
            long userId) throws DatabaseException;

    /**
     * 
     * @param registrarId
     * @return list of those urn:nbn, that are reserved
     */
    public List<UrnNbn> getReservedUrnNbnList(long registrarId)
            throws DatabaseException, UnknownRegistrarException;

    /**
     * 
     * @return Maximal size of batch that can be booked.
     */
    public int getMaxBatchSize();
}