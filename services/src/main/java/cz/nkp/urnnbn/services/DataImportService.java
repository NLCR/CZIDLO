/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.DigDocIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.LoginConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistrarCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public interface DataImportService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataImportService.class.getName());

    /**
     * Creates new intelectual entity (or chooses another present entity if 
     * defined identifiers match) along with new digital instance. 
     * @param data
     * @param login login of user performing this operation
     * @return UrnNbn that has been assigned or the one present in data if it can be used
     * @throws AccessException
     * @throws UrnNotFromRegistrarException
     * @throws UrnUsedException
     * @throws ImportFailedException 
     */
    public UrnNbn importNewRecord(RecordImport data, String login) throws
            AccessException, UrnNotFromRegistrarException,
            UrnUsedException, UnknownRegistrarException,
            DigDocIdentifierCollisionException, UnknownArchiverException, UnknownUserException;

    /**
     * Creates new digital instance for existing digital document.
     * @param instance
     * @param login login of user performing this operation
     * @return digital instance object with id set
     */
    public DigitalInstance addDigitalInstance(DigitalInstance instance, String login) throws
            UnknownUserException, AccessException,
            UnknownDigLibException, UnknownDigDocException;

    public void addRegistrarScopeIdentifier(DigDocIdentifier newId, String login) throws
            UnknownUserException, AccessException,
            UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException;

    public Archiver insertNewArchiver(Archiver archiver, String login) throws
            UnknownUserException, NotAdminException;

    public Registrar insertNewRegistrar(Registrar registrar, String login) throws
            UnknownUserException, NotAdminException,
            RegistrarCollisionException;

    public DigitalLibrary insertNewDigitalLibrary(DigitalLibrary library, long registrarId, String login) throws
            UnknownUserException, AccessException,
            UnknownRegistrarException;

    public Catalog insertNewCatalog(Catalog catalog, long registrarId, String login) throws
            UnknownUserException, AccessException,
            UnknownRegistrarException;

    public User addNewUser(User user, String login) throws
            UnknownUserException, NotAdminException,
            LoginConflictException;
}
