package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.core.dto.Archiver;
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
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

@SuppressWarnings("serial")
public class SearchServiceImpl extends AbstractService implements SearchService {

	private static ArrayList<IntelectualEntityDTO> EMPTY_IE_LIST = new ArrayList<IntelectualEntityDTO>(0);
	private static ArrayList<DigitalInstanceDTO> EMPTY_DI_LIST = new ArrayList<DigitalInstanceDTO>(0);
	private static final int MAX_REQUEST_SIZE = 100;

	@Override
	public ArrayList<IntelectualEntityDTO> getSearchResults(String request) {
		if (request == null || request.isEmpty() || request.length() > MAX_REQUEST_SIZE) {
			return EMPTY_IE_LIST;
		} else if (request.toLowerCase().startsWith("urn:nbn:cz:")) {
			return searchByUrnNbn(request);
		} else {
			return searchByIdentifiers(request);
		}
	}

	private ArrayList<IntelectualEntityDTO> searchByUrnNbn(String request) {
		try {
			UrnNbn urnNbn = UrnNbn.valueOf(request);
			UrnNbnWithStatus urnFetched = readService
					.urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode());
			if (urnFetched.getStatus() == Status.ACTIVE) {
				DigitalDocument digDoc = readService.digDocByInternalId(urnFetched.getUrn().getDigDocId());
				IntelectualEntity entity = readService.entityById(digDoc.getIntEntId());
				ArrayList<IntelectualEntityDTO> result = new ArrayList<IntelectualEntityDTO>(1);
				result.add(transformedEntity(entity));
				return result;
			} else {
				return EMPTY_IE_LIST;
			}
		} catch (Throwable e) {
			log(e.getMessage());
			return EMPTY_IE_LIST;
		}
	}

	private ArrayList<IntelectualEntityDTO> searchByIdentifiers(String request) {
		try {
			List<IntelectualEntity> entities = readService.entitiesByIdValue(request);
			ArrayList<IntelectualEntityDTO> result = new ArrayList<IntelectualEntityDTO>(entities.size());
			for (IntelectualEntity entity : entities) {
				result.add(transformedEntity(entity));
			}
			return result;
		} catch (DatabaseException e) {
			log("error searching for ie", e);
			return EMPTY_IE_LIST;
		}
	}

	private IntelectualEntityDTO transformedEntity(IntelectualEntity entity) {
		Originator originator = getOriginator(entity);
		Publication publication = null;
		SourceDocument srcDoc = null;
		List<IntEntIdentifier> enityIds = getIntEntIdentifiers(entity);
		ArrayList<DigitalDocumentDTO> docs = transformedDigitalDocuments(entity);
		switch (entity.getEntityType()) {
		case MONOGRAPH:
		case MONOGRAPH_VOLUME:
		case PERIODICAL:
		case PERIODICAL_VOLUME:
		case PERIODICAL_ISSUE:
		case OTHER:
		case THESIS:
			publication = getPublication(entity);
			break;
		case ANALYTICAL:
			srcDoc = getSourceDocument(entity);
		}
		return DtoTransformer.transformIntelectualEntity(entity, enityIds, publication, originator, srcDoc, docs);
	}

	private ArrayList<DigitalDocumentDTO> transformedDigitalDocuments(IntelectualEntity entity) {
		List<DigitalDocument> documents = getDigitalDocuments(entity);
		ArrayList<DigitalDocumentDTO> results = new ArrayList<DigitalDocumentDTO>(documents.size());
		for (DigitalDocument digitalDocument : documents) {
			// TODO: dodat urnnbn, identifikatory
			UrnNbn urn = urnNbnOfDocument(digitalDocument);
			Registrar registrar = registrarOfDocument(digitalDocument);
			Archiver archiver = archiverOfDocument(digitalDocument);
			ArrayList<DigitalInstanceDTO> instances = instancesOfDocument(digitalDocument);
			DigitalDocumentDTO transformed = DtoTransformer.transformDigitalDocument(digitalDocument, urn, registrar, archiver, instances);
			if (transformed != null) {
				results.add(transformed);
			}
		}
		return results;
	}

	private List<DigitalDocument> getDigitalDocuments(IntelectualEntity entity) {
		try {
			return readService.digDocsOfIntEnt(entity.getId());
		} catch (DatabaseException e) {
			log("error getting digital documents for ie" + entity.getId());
			return Collections.<DigitalDocument> emptyList();
		}
	}

	private UrnNbn urnNbnOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.urnByDigDocId(digitalDocument.getId());
		} catch (DatabaseException e) {
			log("error getting urn:nbn for dd" + digitalDocument.getId());
			return null;
		}
	}

	private Registrar registrarOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.registrarById(digitalDocument.getRegistrarId());
		} catch (DatabaseException e) {
			log("error getting registrar for dd" + digitalDocument.getId());
			return null;
		}
	}

	private Archiver archiverOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.archiverById(digitalDocument.getArchiverId());
		} catch (DatabaseException e) {
			log("error getting archiver for dd" + digitalDocument.getId());
			return null;
		}
	}

	private ArrayList<DigitalInstanceDTO> instancesOfDocument(DigitalDocument digitalDocument) {
		try {
			List<DigitalInstance> instances = readService.digInstancesByDigDocId(digitalDocument.getId());
			ArrayList<DigitalInstanceDTO> result = new ArrayList<DigitalInstanceDTO>(instances.size());
			for (DigitalInstance instance : instances) {
				DigitalLibrary library = libraryOfInstance(instance);
				DigitalInstanceDTO transformed = DtoTransformer.transformDigitalInstance(instance, library);
				result.add(transformed);
			}
			return result;
		} catch (DatabaseException e) {
			log("error getting digital instances for dd" + digitalDocument.getId());
			return EMPTY_DI_LIST;
		}
	}

	private DigitalLibrary libraryOfInstance(DigitalInstance instance) {
		try {
			return readService.libraryByInternalId(instance.getLibraryId());
		} catch (DatabaseException e) {
			log("error getting digital library for di" + instance.getId());
			return null;
		}
	}

	private Originator getOriginator(IntelectualEntity entity) {
		try {
			return readService.originatorByIntEntId(entity.getId());
		} catch (DatabaseException e) {
			log("error getting originator for ie" + entity.getId());
			return null;
		}
	}

	private List<IntEntIdentifier> getIntEntIdentifiers(IntelectualEntity entity) {
		try {
			return readService.intEntIdentifiersByIntEntId(entity.getId());
		} catch (DatabaseException e) {
			log("error getting identifiers for ie" + entity.getId());
			return Collections.<IntEntIdentifier> emptyList();
		}
	}

	private Publication getPublication(IntelectualEntity entity) {
		try {
			return readService.publicationByIntEntId(entity.getId());
		} catch (DatabaseException e) {
			log("error getting publication for ie" + entity.getId());
			return null;
		}
	}

	private SourceDocument getSourceDocument(IntelectualEntity entity) {
		try {
			return readService.sourceDocumentByIntEntId(entity.getId());
		} catch (DatabaseException e) {
			log("error getting source document for ie" + entity.getId());
			return null;
		}
	}
}