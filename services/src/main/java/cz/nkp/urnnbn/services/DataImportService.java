/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.exceptions.*;

import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public interface DataImportService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataImportService.class.getName());

    /**
     * Creates new intelectual entity along with new digital document.
     *
     * @param importData Data object containing intelectual entity and digital document records
     * @param login      login of user performing this operation
     * @return UrnNbn that has been assigned or the one present in data if it can be used
     * @throws AccessException                            if access right of user to registrar doesn't exist
     * @throws UrnNotFromRegistrarException               If registrar code in urn:nbn and registrar code from importData don't match
     * @throws UrnUsedException                           If this urn:nbn is allready being used
     * @throws UnknownRegistrarException                  if no such registrar with code obtained from importData object exists
     * @throws RegistrarScopeIdentifierCollisionException if registrar scope identifier with same type and value as one of those present in importData already exists (for given registrar)
     * @throws UnknownArchiverException                   if no such archiver (with id obtained from importData) existes
     * @throws UnknownUserException                       if no such user with this login exists
     */
    public UrnNbn registerDigitalDocument(DigDocRegistrationData importData, String login) throws AccessException, UnknownUserException,
            UnknownRegistrarException, UnknownArchiverException, UrnNotFromRegistrarException, RegistrationModeNotAllowedException, UrnUsedException,
            IncorrectPredecessorStatus, RegistrarScopeIdentifierCollisionException;

    /**
     * Creates new digital instance for existing digital document.
     *
     * @param instance
     * @param login    login of user performing this operation
     * @return digital instance object with id set
     * @throws UnknownUserException   if user identified by login doesn't exist
     * @throws AccessException        if user doesn't have right to registrar that owns the digital library (identified by id from DigitalInstance)
     * @throws UnknownDigLibException if digital library identified by id obtained from DigitalInstance doesn't exist
     * @throws UnknownDigDocException if digital document (identified by id obtained from DigitalInstance) doesn't exist
     */
    public DigitalInstance addDigitalInstance(DigitalInstance instance, String login) throws UnknownUserException, AccessException,
            UnknownDigLibException, UnknownDigDocException;

    public void addRegistrarScopeIdentifier(RegistrarScopeIdentifier newId, String login) throws UnknownUserException, AccessException,
            UnknownRegistrarException, UnknownDigDocException, RegistrarScopeIdentifierCollisionException;

    public Archiver insertNewArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException;

    public Registrar insertNewRegistrar(Registrar registrar, String login) throws UnknownUserException, NotAdminException,
            RegistrarCollisionException;

    public DigitalLibrary insertNewDigitalLibrary(DigitalLibrary library, long registrarId, String login) throws UnknownUserException,
            AccessException, UnknownRegistrarException;

    public Catalog insertNewCatalog(Catalog catalog, long registrarId, String login) throws UnknownUserException, AccessException,
            UnknownRegistrarException;

    public User addNewUser(User user, String login) throws UnknownUserException, NotAdminException, LoginConflictException;

    public void addRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException,
            RegistrarRightCollisionException, UnknownRegistrarException;
}
