/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import java.util.List;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnReservationService extends BusinessService {

    /**
     * This operation reserves certain number of urnNbns. UrnNbns that are reserved are not used for assigning by the application. The registrar is
     * expected to put those urnNbns into its metadata records that are ready for import into the Czidlo. When one of those records is imported, the
     * application assignes urnNbn, that is present in metadata, to newly created digital instance.
     *
     * @param batchSize
     *            number of urnNbn identifiers returned
     * @param registrar
     * @param login
     *            Login of user to perform this operation.
     * @return Batch of urnNbn of size batchSize.
     */
    public List<UrnNbn> reserveUrnNbnBatch(int batchSize, Registrar registrar, String login) throws UnknownUserException, AccessException;

    /**
     *
     * @param registrarId
     * @return list of those urn:nbn, that are reserved
     */
    public List<UrnNbn> getReservedUrnNbnList(long registrarId) throws UnknownRegistrarException;

    /**
     *
     * @param urn
     * @return true if this urn is reserved
     */
    public boolean isReserved(UrnNbn urn);

    /**
     *
     * @return Maximal size of batch that can be booked.
     */
    public int getMaxBatchSize();
}