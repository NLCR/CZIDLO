/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataRemoveService {

    public void removeDigitalDocumentIdentifiers(long digRepId) throws UnknownDigDocException;

    public void removeDigitalDocumentId(long digRepId, DigDocIdType type) throws UnknownDigDocException, DigRepIdNotDefinedException;

    public void removeArchiver(long archiverId) throws UnknownArchiverException, CannotBeRemovedException;

    public void removeRegistrar(long registrarId) throws UnknownRegistrarException, CannotBeRemovedException;
}
