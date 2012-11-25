        /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 * TODO: zbavit se DatabaseException (resp. přeložit na RuntimeException)
 *
 * @author Martin Řehánek
 */
public interface DataAccessService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataAccessService.class.getName());

    public UrnNbnWithStatus urnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode, boolean withPredecessorsAndSuccessors) throws DatabaseException;

    public DigitalDocument digDocByInternalId(long digDocId) throws DatabaseException;

    public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) throws DatabaseException;

    public List<DigDocIdentifier> digDocIdentifiersByDigDocId(long digDocId) throws DatabaseException;

    public Registrar registrarById(long id) throws DatabaseException;

    public Archiver archiverById(long id) throws DatabaseException;

    public List<Archiver> archivers() throws DatabaseException;

    public IntelectualEntity entityById(long id) throws DatabaseException;

    public List<IntelectualEntity> entitiesByIdValue(String value) throws DatabaseException;

    public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public Publication publicationByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public Originator originatorByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public SourceDocument sourceDocumentByIntEntId(long intEntId) throws DatabaseException;

    public Registrar registrarByCode(RegistrarCode code) throws DatabaseException;

    public List<DigitalLibrary> librariesByRegistrarId(long registrarId) throws DatabaseException;

    public List<Catalog> catalogsByRegistrarId(long registrarId) throws DatabaseException;

    public List<Catalog> catalogs() throws DatabaseException;

    public List<Registrar> registrars() throws DatabaseException;

    public int digitalDocumentsCount(long registrarId) throws DatabaseException;

    public DigitalDocument digDocByIdentifier(DigDocIdentifier id) throws DatabaseException;

    public List<DigitalDocument> digDocsOfIntEnt(long intEntId) throws DatabaseException;

    public long digitalInstancesCount() throws DatabaseException;

    public DigitalInstance digInstanceByInternalId(long id) throws DatabaseException;

    public List<DigitalInstance> digInstancesByDigDocId(long digDocId) throws DatabaseException;

    public DigitalLibrary libraryByInternalId(long libraryId) throws DatabaseException;

    /**
     *
     * @param login login of user performing this operation
     * @param includePasswords
     * @return
     * @throws UnknownUserException
     * @throws NotAdminException
     */
    public List<User> users(String login, boolean includePasswords)
            throws UnknownUserException, NotAdminException;

    /**
     *
     * @param login login of user that is being lookuped
     * @param includePassword
     * @return
     * @throws UnknownUserException
     */
    public User userByLogin(String login, boolean includePassword)
            throws UnknownUserException;

    /**
     *
     * @param userId
     * @param login login of user performing this operation
     * @return
     * @throws UnknownUserException
     * @throws NotAdminException
     */
    public List<Registrar> registrarsManagedByUser(long userId, String login)
            throws UnknownUserException, NotAdminException;

    public Set<UrnNbn> urnNbnsOfChangedRecords(DateTime from, DateTime until);

    public Set<UrnNbn> urnNbnsOfChangedRecordsOfRegistrar(Registrar registrar, DateTime from, DateTime until);
}
