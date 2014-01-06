/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.exceptions.ContentNotFoundException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 * 
 * @author Martin Řehánek
 */
public class DataAccessServiceImpl extends BusinessServiceImpl implements DataAccessService {

	public DataAccessServiceImpl(DatabaseConnector con) {
		super(con);
	}

	@Override
	public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
		try {
			UrnNbn urn = factory.urnDao().getUrnNbnByDigDocId(id);
			if (withPredecessorsAndSuccessors) {
				urn.setPredecessors(factory.urnDao().getPredecessors(urn));
				urn.setSuccessors(factory.urnDao().getSuccessors(urn));
			}
			return urn;
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public UrnNbnWithStatus urnByRegistrarCodeAndDocumentCode(RegistrarCode code, String documentCode, boolean withPredecessorsAndSuccessors) {
		try {
			UrnNbn urnNbn = factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(code, documentCode);
			if (withPredecessorsAndSuccessors) {
				urnNbn.setPredecessors(factory.urnDao().getPredecessors(urnNbn));
				urnNbn.setSuccessors(factory.urnDao().getSuccessors(urnNbn));
			}
			if (urnNbn.isActive()) {
				return new UrnNbnWithStatus(urnNbn, UrnNbnWithStatus.Status.ACTIVE, null);
			} else {
				return new UrnNbnWithStatus(urnNbn, UrnNbnWithStatus.Status.DEACTIVATED, null);
			}
		} catch (RecordNotFoundException ex) { // urn:nb not in table urnNbn
			try {
				UrnNbn urnNbnReserved = factory.urnReservedDao().getUrn(code, documentCode);
				return new UrnNbnWithStatus(urnNbnReserved, UrnNbnWithStatus.Status.RESERVED, null);
			} catch (RecordNotFoundException ex2) { // urn:nbn also not reserved
				UrnNbn urn = new UrnNbn(code, documentCode, null, null);
				return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.FREE, null);
			} catch (DatabaseException ex3) {
				throw new RuntimeException(ex3);
			}
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public DigitalDocument digDocByInternalId(long digDocId) {
		try {
			return factory.documentDao().getDocumentByDbId(digDocId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<RegistrarScopeIdentifier> registrarScopeIdentifiers(long id) {
		try {
			return factory.digDocIdDao().getRegistrarScopeIds(id);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<RegistrarScopeIdentifier> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public RegistrarScopeIdentifier registrarScopeIdentifier(long digDocId, RegistrarScopeIdType type)
			throws RegistrarScopeIdentifierNotDefinedException {
		List<RegistrarScopeIdentifier> identifiers = registrarScopeIdentifiers(digDocId);
		for (RegistrarScopeIdentifier id : identifiers) {
			if (type.equals(id.getType())) {
				return id;
			}
		}
		throw new RegistrarScopeIdentifierNotDefinedException(type);
	}

	@Override
	public Registrar registrarById(long id) {
		try {
			return factory.registrarDao().getRegistrarById(id);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Archiver archiverById(long id) {
		try {
			return factory.archiverDao().getArchiverById(id);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public IntelectualEntity entityById(long id) {
		try {
			return factory.intelectualEntityDao().getEntityByDbId(id);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<IntelectualEntity> entitiesByIdValue(String value) {
		try {
			List<Long> idList = factory.intelectualEntityDao().getEntitiesDbIdListByIdentifierValue(value);
			List<IntelectualEntity> result = new ArrayList<IntelectualEntity>(idList.size());
			for (long id : idList) {
				IntelectualEntity entity = entityById(id);
				if (entity != null) {
					result.add(entity);
				}
			}
			return result;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

//	@Override
//	public List<IntelectualEntity> entitiesByIdValueWithFullTextSearch(String value) {
//		try {
//			// TODO: put back
//			int limit = 500;
//			List<Long> wholeIdList = factory.intelectualEntityDao().getEntitiesDbIdListByIdentifierValueWithFullTextSearch(value, 0, limit);
//			int last = Math.min(limit, wholeIdList.size());
//			List<Long> idList = wholeIdList.subList(0, last);
//
//			List<IntelectualEntity> result = new ArrayList<IntelectualEntity>(idList.size());
//			for (long id : idList) {
//				IntelectualEntity entity = entityById(id);
//				if (entity != null) {
//					result.add(entity);
//				}
//			}
//			return result;
//		} catch (DatabaseException ex) {
//			throw new RuntimeException(ex);
//		}
//	}

	@Override
	public List<Long> entitiesIdsByIdValueWithFullTextSearch(String value, int hardLimit) {
		try {
			return factory.intelectualEntityDao().getEntitiesDbIdListByIdentifierValueWithFullTextSearch(value, 0, hardLimit);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<IntelectualEntity> entitiesByIdValues(List<Long> ids) {
		List<IntelectualEntity> result = new ArrayList<IntelectualEntity>(ids.size());
		for (long id : ids) {
			IntelectualEntity entity = entityById(id);
			if (entity != null) {
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId) {
		try {
			return factory.intEntIdentifierDao().getIdList(intEntId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<IntEntIdentifier> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Publication publicationByIntEntId(long intEntId) {
		try {
			return factory.publicationDao().getPublicationById(intEntId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.FINE, "no publication found for entity {0}", intEntId);
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Originator originatorByIntEntId(long intEntId) {
		try {
			return factory.originatorDao().getOriginatorById(intEntId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.FINE, "no primary originator found for entity {0}", intEntId);
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public SourceDocument sourceDocumentByIntEntId(long intEntId) {
		try {
			return factory.srcDocDao().getSrcDocById(intEntId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.FINE, "no souce document found for entity {0}", intEntId);
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Registrar registrarByCode(RegistrarCode code) {
		try {
			return factory.registrarDao().getRegistrarByCode(code);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<DigitalLibrary> librariesByRegistrarId(long registrarId) {
		try {
			return factory.diglLibDao().getLibraries(registrarId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<DigitalLibrary> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Catalog> catalogs() {
		try {
			return factory.catalogDao().getCatalogs();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Catalog> catalogsByRegistrarId(long registrarId) {
		try {
			return factory.catalogDao().getCatalogs(registrarId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<Catalog> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Registrar> registrars() {
		try {
			return factory.registrarDao().getAllRegistrars();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public int digitalDocumentsCount(long registrarId) {
		try {
			return factory.documentDao().getDigDocCount(registrarId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return 0;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public DigitalDocument digDocByIdentifier(RegistrarScopeIdentifier id) {
		try {
			Long digDocId = factory.documentDao().getDigDocIdByRegistrarScopeId(id);
			return factory.documentDao().getDocumentByDbId(digDocId);
		} catch (RecordNotFoundException ex) {
			// logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<DigitalDocument> digDocsOfIntEnt(long intEntId) {
		try {
			return factory.documentDao().getDocumentsOfIntEntity(intEntId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<DigitalDocument> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long digitalInstancesCount() {
		try {
			return factory.digInstDao().getTotalCount();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<DigitalInstance> digInstancesByDigDocId(long digDocId) {
		try {
			return factory.digInstDao().getDigitalInstancesOfDigDoc(digDocId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return Collections.<DigitalInstance> emptyList();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public DigitalInstance digInstanceByInternalId(long id) {
		try {
			return factory.digInstDao().getDigInstanceById(id);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public DigitalLibrary libraryByInternalId(long libraryId) {
		try {
			return factory.diglLibDao().getLibraryById(libraryId);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			return null;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Archiver> archivers() {
		try {
			List<Archiver> includingRegistrars = factory.archiverDao().getAllArchivers();
			List<Long> registrarIdList = factory.registrarDao().getAllRegistrarsId();
			List<Archiver> result = new ArrayList<Archiver>(includingRegistrars.size() - registrarIdList.size());
			for (Archiver archiverOrRegistrar : includingRegistrars) {
				if (!registrarIdList.contains(archiverOrRegistrar.getId())) {
					result.add(archiverOrRegistrar);
				}
			}
			return result;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<User> users(String login) throws UnknownUserException, NotAdminException {
		try {
			authorization.checkAdminRights(login);
			return factory.userDao().getAllUsers();
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public User userByLogin(String login) throws UnknownUserException {
		try {
			return factory.userDao().getUserByLogin(login);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownUserException(login);
		}
	}

	@Override
	public List<Registrar> registrarsManagedByUser(long userId, String login) throws UnknownUserException, NotAdminException {
		try {
			return factory.registrarDao().getRegistrarsManagedByUser(userId);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownUserException(userId);
		}
	}

	@Override
	public Set<UrnNbn> urnNbnsOfChangedRecordsOfRegistrar(Registrar registrar, DateTime from, DateTime until) {
		try {
			Set<DigitalDocument> foundDigitalDocuments = new HashSet<DigitalDocument>();
			// 1. vsechny zmenene DD, pak odfiltrovat podle id registratora, vytahnout urn:nbn
			foundDigitalDocuments.addAll(findChangedDigDocs(registrar, from, until));
			// 2. vsechny zmenene identifikatory DD a pro kazdy nacist DD
			foundDigitalDocuments.addAll(findDigDocsWithChangedIdentifier(from, until));
			// 3. digitalni dokumenty zmenenych dig. instanci
			foundDigitalDocuments.addAll(findDigDocsOfChangedDigInstances(from, until));
			// 4. digitalni dokumenty zmenenych int. entit
			foundDigitalDocuments.addAll(findDigDocsOfChangedIntEntities(from, until));
			// remove dig docs of other registrars
			Set<DigitalDocument> digDocsOfRegistrar = removeDigDocsOfOtherRegistrars(foundDigitalDocuments, registrar);
			Set<UrnNbn> result = toUrnNbnSet(digDocsOfRegistrar);
			// add changed urn:nbns
			result.addAll(findChangedUrnNbns(registrar, from, until));
			return result;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<UrnNbn> urnNbnsOfRegistrar(RegistrarCode registrarCode) {
		try {
			return factory.urnDao().getUrnNbnsByRegistrarCode(registrarCode);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Set<UrnNbn> urnNbnsOfChangedRecords(DateTime from, DateTime until) {
		try {
			Set<DigitalDocument> foundDigitalDocuments = new HashSet<DigitalDocument>();
			// 1. vsechny zmenene DD, pak odfiltrovat podle id registratora, vytahnout urn:nbn
			foundDigitalDocuments.addAll(findChangedDigDocs(from, until));
			// 2. vsechny zmenene identifikatory DD a pro kazdy nacist DD
			foundDigitalDocuments.addAll(findDigDocsWithChangedIdentifier(from, until));
			// 3. digitalni dokumenty zmenenych dig. instanci
			foundDigitalDocuments.addAll(findDigDocsOfChangedDigInstances(from, until));
			// 4. digitalni dokumenty zmenenych int. entit
			foundDigitalDocuments.addAll(findDigDocsOfChangedIntEntities(from, until));
			Set<UrnNbn> result = toUrnNbnSet(foundDigitalDocuments);
			// add changed urn:nbns
			result.addAll(findChangedUrnNbns(from, until));
			return result;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Content contentByNameAndLanguage(String name, String language) throws ContentNotFoundException {
		try {
			Content content = factory.contentDao().getContentByNameAndLanguage(name, language);
			return content;
		} catch (DatabaseException ex) {
			throw new RuntimeException("database error", ex);
		} catch (RecordNotFoundException ex) {
			throw new ContentNotFoundException(language, name, ex);
		}
	}

	@Override
	public List<UrnNbnExport> selectByCriteria(String languageCode, UrnNbnExportFilter filter, boolean withDigitalInstances) {
		try {
			return factory.urnDao().selectByCriteria(languageCode, filter, withDigitalInstances);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	private Collection<DigitalDocument> findDigDocsOfChangedIntEntities(DateTime from, DateTime until) throws DatabaseException {
		List<Long> entityDbList = factory.intelectualEntityDao().getEntitiesDbIdListByTimestamps(from, until);
		Set<DigitalDocument> result = new HashSet<DigitalDocument>();
		for (Long entityId : entityDbList) {
			result.addAll(digDocsOfIntEnt(entityId));
		}
		return result;
	}

	private List<DigitalDocument> findChangedDigDocs(Registrar registrar, DateTime from, DateTime until) throws DatabaseException {
		try {
			return factory.documentDao().getDigDocsByRegistrarIdAndTimestamps(registrar.getId(), from, until);
		} catch (RecordNotFoundException ex) {
			logger.log(Level.SEVERE, null, ex);
			return Collections.<DigitalDocument> emptyList();
		}
	}

	private List<DigitalDocument> findChangedDigDocs(DateTime from, DateTime until) throws DatabaseException {
		return factory.documentDao().getDigDocsByTimestamps(from, until);
	}

	private Set<DigitalDocument> findDigDocsWithChangedIdentifier(DateTime from, DateTime until) throws DatabaseException {
		List<RegistrarScopeIdentifier> identifiers = factory.digDocIdDao().getRegistrarScopeIdsByTimestamps(from, until);
		Set<DigitalDocument> result = new HashSet<DigitalDocument>();
		for (RegistrarScopeIdentifier id : identifiers) {
			try {
				result.add(factory.documentDao().getDocumentByDbId(id.getDigDocId()));
			} catch (RecordNotFoundException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}

	private Set<DigitalDocument> findDigDocsOfChangedDigInstances(DateTime from, DateTime until) throws DatabaseException {
		List<DigitalInstance> digInstances = factory.digInstDao().getDigitalInstancesByTimestamps(from, until);
		Set<DigitalDocument> result = new HashSet<DigitalDocument>();
		for (DigitalInstance digInst : digInstances) {
			try {
				result.add(factory.documentDao().getDocumentByDbId(digInst.getDigDocId()));
			} catch (RecordNotFoundException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}

	private Set<DigitalDocument> removeDigDocsOfOtherRegistrars(Set<DigitalDocument> original, Registrar registrar) {
		Set<DigitalDocument> result = new HashSet<DigitalDocument>(original.size());
		for (DigitalDocument doc : original) {
			if (doc.getRegistrarId() == registrar.getId()) {
				result.add(doc);
			}
		}
		return result;
	}

	private Set<UrnNbn> toUrnNbnSet(Set<DigitalDocument> docs) throws DatabaseException {
		Set<UrnNbn> result = new HashSet<UrnNbn>(docs.size());
		for (DigitalDocument doc : docs) {
			try {
				result.add(factory.urnDao().getUrnNbnByDigDocId(doc.getId()));
			} catch (RecordNotFoundException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}

	private List<UrnNbn> findChangedUrnNbns(DateTime from, DateTime until) throws DatabaseException {
		return factory.urnDao().getUrnNbnsByTimestamps(from, until);
	}

	private List<UrnNbn> findChangedUrnNbns(Registrar registrar, DateTime from, DateTime until) throws DatabaseException {
		return factory.urnDao().getUrnNbnsByRegistrarCodeAndTimestamps(registrar.getCode(), from, until);
	}

}
