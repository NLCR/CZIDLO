/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.DigDocIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataRemoveService {

    //TODO: access rights
    public void removeDigitalDocumentIdentifiers(long digDocId) throws UnknownDigDocException;

    //TODO: access rights
    public void removeDigitalDocumentId(long digDocId, DigDocIdType type) throws UnknownDigDocException, DigDocIdNotDefinedException;

    public void removeArchiver(long archiverId, String login) throws
            UnknownUserException, NotAdminException,
            UnknownArchiverException, CannotBeRemovedException;

    public void removeRegistrar(long registrarId, String login) throws
            UnknownUserException, NotAdminException,
            UnknownRegistrarException, CannotBeRemovedException;

    public void removeDigitalLibrary(long libraryId, String login) throws
            UnknownUserException, AccessException,
            UnknownDigLibException, CannotBeRemovedException;

    public void removeCatalog(long libraryId, String login) throws
            UnknownUserException, AccessException,
            UnknownCatalogException;

    public void removeUser(long userId, String login) throws
            UnknownUserException, NotAdminException;
    
    public void removeRegistrarRight(long userId, long registarId, String login) throws
            UnknownUserException, NotAdminException,
            UnknownRegistrarException;
    
    //TODO: update digital document datastamp
    public void removeDigitalInstance(long instanceId, String login) throws
            UnknownUserException, AccessException,
            UnknownDigInstException;
}
