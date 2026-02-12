/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.exceptions.*;

import java.util.Collection;

/**
 * @author Martin Řehánek
 */
public interface DataUpdateService extends BusinessService {

    /**
     * @param login
     * @param rsId
     * @throws UnknownUserException                        if user identified by login doesn't exist
     * @throws AccessException                             if user doesn't have right to registrar (identified by rsId.getRegistrarId())
     * @throws UnknownRegistrarException                   if no such registrar with id form rsId.getRegistrarId() exists
     * @throws UnknownDigDocException                      if digital document (identified by rsId.getRegistrarId()) doesn't exist
     * @throws RegistrarScopeIdentifierCollisionException  if there already exists another digital document with same type and value (within registrar)
     * @throws RegistrarScopeIdentifierNotDefinedException if no registrar-scope of such type exist for digital document
     */
    public void updateRegistrarScopeIdentifier(String login, RegistrarScopeIdentifier rsId) throws UnknownUserException, AccessException,
            UnknownRegistrarException, UnknownDigDocException,
            RegistrarScopeIdentifierCollisionException, RegistrarScopeIdentifierNotDefinedException;

    public void updateDigitalDocument(DigitalDocument doc, String login) throws UnknownUserException, AccessException, UnknownDigDocException;

    public void updateDigitalInstance(DigitalInstance instance, String login) throws UnknownUserException, AccessException, UnknownDigInstException;

    public void updateRegistrar(Registrar registrar, String login) throws UnknownUserException, AccessException, UnknownRegistrarException;

    public void updateArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException, UnknownArchiverException;

    public void updateDigitalLibrary(DigitalLibrary library, String login) throws UnknownUserException, AccessException, UnknownDigLibException;

    public void updateCatalog(Catalog catalog, String login) throws UnknownUserException, AccessException, UnknownCatalogException;

    public void updateIntelectualEntity(IntelectualEntity entity, Originator originator, Publication publication, SourceDocument srcDoc,
                                        Collection<IntEntIdentifier> identifiers, String login) throws UnknownUserException, AccessException,
            UnknownIntelectualEntity;

    public void updateUser(User user, String login) throws UnknownUserException, AccessException;

    public void updateUserSelf(User user, String login) throws UnknownUserException, InvalidUserException;

    public void updateContent(Content content, String login) throws UnknownUserException, NotAdminException, ContentNotFoundException;

    /**
     * Deactivates digital instance. Deactivated digital instance is visible through web interface and api (with information about it being
     * deactivated), but it is no longer used for resolving.
     *
     * @param instanceId global identifier of digital instance
     * @param login      login of user performing this operation
     * @throws UnknownUserException    if no such user with this login exists
     * @throws AccessException         if user doesn't have access right to the registrar that owns digital library that the digital instance is in
     * @throws UnknownDigInstException if no digital instance with this instanceId exists
     */
    public void deactivateDigitalInstance(long instanceId, String login) throws UnknownUserException, AccessException, UnknownDigInstException;

    /**
     * Deactivates URN:NBN. Record of deactivated URN:NBN will be still present but resolvation algorithm never redirect to any digital instance of
     * deactivated URN:NBN.
     *
     * @param urn
     * @param login login of user performing this operation
     * @param note  note describing reasons for deactivation
     * @throws UnknownUserException
     * @throws AccessException
     * @throws UnknownDigDocException
     */
    public void deactivateUrnNbn(UrnNbn urn, String login, String note) throws UnknownUserException, AccessException, UnknownDigDocException;

    /**
     * Reactivates URN:NBN. Reactivated URN:NBN can be resolved to its digital instances again.
     *
     * @param urn
     * @param login login of user performing this operation
     * @throws UnknownUserException
     * @throws AccessException
     * @throws UnknownDigDocException
     */
    public void reactivateUrnNbn(UrnNbn urn, String login) throws UnknownUserException, AccessException, UnknownDigDocException;

    /**
     * Adds relation predecessor-successor between two URN:NBNs.
     *
     * @param predecessor
     * @param successor
     * @param note
     * @param login
     * @throws UnknownUserException
     * @throws NotAdminException
     * @throws IncorrectPredecessorStatus
     */
    public void addRelationPredecessorSuccessor(UrnNbn predecessor, UrnNbn successor, String note, String login) throws UnknownUserException, NotAdminException, IncorrectPredecessorStatus;

    /**
     * Removes relation predecessor-successor between two URN:NBNs.
     *
     * @param predecessor
     * @param successor
     * @param login
     * @throws UnknownUserException
     * @throws NotAdminException
     */
    public void removeRelationPredecessorSuccessor(UrnNbn predecessor, UrnNbn successor, String login) throws UnknownUserException, NotAdminException;

}
