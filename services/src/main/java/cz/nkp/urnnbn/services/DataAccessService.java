package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.*;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.exceptions.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * TODO: zbavit se DatabaseException (resp. přeložit na RuntimeException)
 *
 * @author Martin Řehánek
 */
public interface DataAccessService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataAccessService.class.getName());

    public UrnNbnWithStatus urnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode, boolean withPredecessorsAndSuccessors);

    public DigitalDocument digDocByInternalId(long digDocId);

    public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors);

    public List<RegistrarScopeIdentifier> registrarScopeIdentifiers(long digDocId);

    public RegistrarScopeIdentifier registrarScopeIdentifier(long digDocId, RegistrarScopeIdType type)
            throws RegistrarScopeIdentifierNotDefinedException;

    public Registrar registrarById(long id);

    public Archiver archiverById(long id);

    public List<Archiver> archivers();

    public IntelectualEntity entityById(long id);

    public List<IntelectualEntity> entitiesByIdValue(String value);

    public List<IntelectualEntity> entitiesByIdValues(List<Long> ids);

    public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId);

    // pokud nenajde, vrati null
    public Publication publicationByIntEntId(long intEntId);

    // pokud nenajde, vrati null
    public Originator originatorByIntEntId(long intEntId);

    // pokud nenajde, vrati null
    public SourceDocument sourceDocumentByIntEntId(long intEntId);

    public Registrar registrarByCode(RegistrarCode code);

    public List<DigitalLibrary> librariesByRegistrarId(long registrarId);

    public List<Catalog> catalogsByRegistrarId(long registrarId);

    public Catalog catalogByInternalId(long catalogId);

    public List<Catalog> catalogs();

    public List<Registrar> registrars();

    public int digitalDocumentsCount(long registrarId);

    public DigitalDocument digDocByIdentifier(RegistrarScopeIdentifier id);

    public List<DigitalDocument> digDocsByIsbn(String isbn);

    public List<DigitalDocument> digDocsByIssn(String issn);

    public List<DigitalDocument> digDocsOfIntEnt(long intEntId);

    public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until);

    public List<ResolvationLog> resolvationLogsByDate(DateTime from, DateTime until);

    public long digitalInstancesCount();

    public DigitalInstance digInstanceByInternalId(long id);

    public List<DigitalInstance> digInstancesByDigDocId(long digDocId);

    public List<DigitalInstance> digInstancesByUrl(String url);

    public DigitalLibrary libraryByInternalId(long libraryId);

    /**
     * @param login login of user performing this operation
     * @return
     * @throws UnknownUserException
     * @throws NotAdminException
     */
    public List<User> users(String login) throws UnknownUserException, NotAdminException;

    /**
     * @param userId id of user that is being lookuped
     * @return
     * @throws UnknownUserException
     */
    public User userById(long userId) throws UnknownUserException;

    /**
     * @param login login of user that is being lookuped
     * @return
     * @throws UnknownUserException
     */
    public User userByLogin(String login) throws UnknownUserException;

    /**
     * @param userId
     * @param login  login of user performing this operation
     * @return
     * @throws UnknownUserException
     * @throws NotAdminException
     */
    public List<Registrar> registrarsManagedByUser(long userId, String login) throws UnknownUserException, AccessException;

    public Set<UrnNbn> urnNbnsOfChangedRecords(DateTime from, DateTime until);

    public Set<UrnNbn> urnNbnsOfChangedRecordsOfRegistrar(Registrar registrar, DateTime from, DateTime until);

    public List<UrnNbn> urnNbnsOfRegistrar(RegistrarCode registrarCode);

    public Content contentByNameAndLanguage(String name, String language) throws ContentNotFoundException;

    public List<UrnNbnExport> selectByCriteria(String languageCode, UrnNbnExportFilter filter, boolean withDigitalInstances);

    public List<DiExport> listDiExport(List<String> registrarCodes, List<String> entityTypes, boolean includeUrnActive,
                                       boolean includeUrnDeactivated, boolean includeDiActive, boolean includeDiDeactivated);

}
