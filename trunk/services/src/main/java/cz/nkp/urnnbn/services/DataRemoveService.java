/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
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

    public void removeDigitalDocumentIdentifiers(long digDocId, String login) throws
            UnknownUserException, AccessException,
            UnknownDigDocException;

    public void removeDigitalDocumentId(long digDocId, RegistrarScopeIdType type, String login) throws
            UnknownUserException, AccessException,
            UnknownDigDocException, RegistrarScopeIdentifierNotDefinedException;

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

    /**
     * Deactivates digital instance. Deactivated digital instance is visible
     * through web interface and api (with information about it being
     * deactivated), but it is no longer used for resolving.
     *
     * @param instanceId global identifier of digital instance
     * @param login login of user performing this operation
     * @throws UnknownUserException if no such user with this login exists
     * @throws AccessException if user doesn't have access right to the
     * registrar that owns digital library that the digital instance is in
     * @throws UnknownDigInstException if no digital instance with this
     * instanceId exists
     */
    public void deactivateDigitalInstance(long instanceId, String login) throws
            UnknownUserException, AccessException,
            UnknownDigInstException;

    /**
     * Deactivates URN:NBN. Record of deactivated URN:NBN will be still present
     * but resolvation algorithm never redirect to any digital instance of deactivated URN:NBN.
     *
     * @param urn
     * @param login login of user performing this operation
     * @param note note describing reasons for deactivation
     * @throws UnknownUserException
     * @throws AccessException
     * @throws UnknownDigDocException
     */
    public void deactivateUrnNbn(UrnNbn urn, String login, String note) throws
            UnknownUserException, AccessException,
            UnknownDigDocException;
}
