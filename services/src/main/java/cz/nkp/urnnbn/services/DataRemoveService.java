/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataRemoveService {

    public void removeDigitalDocumentIdentifiers(long digRepId) throws UnknownDigDocException;

    public void removeDigitalDocumentId(long digRepId, DigDocIdType type) throws UnknownDigDocException, DigRepIdNotDefinedException;
}
