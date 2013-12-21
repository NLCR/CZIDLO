package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class SearchServiceImpl extends AbstractService implements SearchService {

	private static final long serialVersionUID = 1750995108864579331L;
	private static final Logger logger = Logger.getLogger(SearchServiceImpl.class.getName());
	private static final int MAX_REQUEST_SIZE = 200;
	private static ArrayList<IntelectualEntityDTO> EMPTY_IE_LIST = new ArrayList<IntelectualEntityDTO>(0);
	private static ArrayList<DigitalInstanceDTO> EMPTY_DI_LIST = new ArrayList<DigitalInstanceDTO>(0);

	@Override
	public ArrayList<IntelectualEntityDTO> getSearchResults(String searchRequest) throws ServerException {
		try {
			if (searchRequest == null || searchRequest.isEmpty() || searchRequest.length() > MAX_REQUEST_SIZE) {
				// System.err.println("found 0 records (empty or too long search request)");
				return EMPTY_IE_LIST;
			} else if (searchRequest.toLowerCase().startsWith("urn:nbn:cz:")) {
				return new ArrayList<IntelectualEntityDTO>(searchByUrnNbn(searchRequest));
			} else {
				return new ArrayList<IntelectualEntityDTO>(searchByIdentifiers(searchRequest));
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, null, e);
			throw new ServerException(e.getMessage());
		}
	}

	private Set<IntelectualEntityDTO> searchByUrnNbn(String request) {
		UrnNbn urnNbn = UrnNbn.valueOf(request);
		UrnNbnWithStatus urnFetched = readService.urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(),
				true);
		if (urnFetched.getStatus() == Status.ACTIVE || urnFetched.getStatus() == Status.DEACTIVATED) {
			DigitalDocument digDoc = readService.digDocByInternalId(urnFetched.getUrn().getDigDocId());
			// Set allways contains just single item
			IntelectualEntity entity = readService.entityById(digDoc.getIntEntId());
			Set<IntelectualEntityDTO> result = new HashSet<IntelectualEntityDTO>();
			result.add(transformedEntity(entity));
			return result;
		} else {
			return Collections.<IntelectualEntityDTO> emptySet();
		}
	}

	private Set<IntelectualEntityDTO> searchByIdentifiers(String request) {
		request = request.replaceAll(":", " ");
		String[] words = request.split(" ");
		String sep = "";
		StringBuilder query = new StringBuilder();
		for (String word : words) {
			if (!word.trim().isEmpty()) {
				query.append(sep).append(word.trim());
				sep = " &";
			}
		}
		List<IntelectualEntity> entities = readService.entitiesByIdValueWithFullTextSearch(query.toString());
		Set<IntelectualEntityDTO> result = new HashSet<IntelectualEntityDTO>();
		for (IntelectualEntity entity : entities) {
			result.add(transformedEntity(entity));
		}
		return result;
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
			UrnNbn urn = urnNbnOfDocument(digitalDocument);
			Registrar registrar = registrarOfDocument(digitalDocument);
			Archiver archiver = archiverOfDocument(digitalDocument);
			ArrayList<DigitalInstanceDTO> instances = instancesOfDocument(digitalDocument);
			List<RegistrarScopeIdentifier> registrarScopeIds = getRegistrarScopeIds(digitalDocument);
			DigitalDocumentDTO transformed = DtoTransformer.transformDigitalDocument(digitalDocument, urn, registrar, archiver, instances,
					registrarScopeIds);
			if (transformed != null) {
				results.add(transformed);
			}
		}
		return results;
	}

	private List<RegistrarScopeIdentifier> getRegistrarScopeIds(DigitalDocument digitalDocument) {
		try {
			return readService.registrarScopeIdentifiers(digitalDocument.getId());
		} catch (Throwable e) {
			log("error getting registrar-scope identifiers of dd " + digitalDocument.getId());
			return Collections.<RegistrarScopeIdentifier> emptyList();
		}
	}

	private List<DigitalDocument> getDigitalDocuments(IntelectualEntity entity) {
		try {
			return readService.digDocsOfIntEnt(entity.getId());
		} catch (Throwable e) {
			log("error getting digital documents for ie" + entity.getId());
			return Collections.<DigitalDocument> emptyList();
		}
	}

	private UrnNbn urnNbnOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.urnByDigDocId(digitalDocument.getId(), true);
		} catch (Throwable e) {
			log("error getting urn:nbn for dd" + digitalDocument.getId());
			return null;
		}
	}

	private Registrar registrarOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.registrarById(digitalDocument.getRegistrarId());
		} catch (Throwable e) {
			log("error getting registrar for dd" + digitalDocument.getId());
			return null;
		}
	}

	private Archiver archiverOfDocument(DigitalDocument digitalDocument) {
		try {
			return readService.archiverById(digitalDocument.getArchiverId());
		} catch (Throwable e) {
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
		} catch (Throwable e) {
			log("error getting digital instances for dd" + digitalDocument.getId());
			return EMPTY_DI_LIST;
		}
	}

	private DigitalLibrary libraryOfInstance(DigitalInstance instance) {
		try {
			return readService.libraryByInternalId(instance.getLibraryId());
		} catch (Throwable e) {
			log("error getting digital library for di" + instance.getId());
			return null;
		}
	}

	private Originator getOriginator(IntelectualEntity entity) {
		try {
			return readService.originatorByIntEntId(entity.getId());
		} catch (Throwable e) {
			log("error getting originator for ie" + entity.getId());
			return null;
		}
	}

	private List<IntEntIdentifier> getIntEntIdentifiers(IntelectualEntity entity) {
		try {
			return readService.intEntIdentifiersByIntEntId(entity.getId());
		} catch (Throwable e) {
			log("error getting identifiers for ie" + entity.getId());
			return Collections.<IntEntIdentifier> emptyList();
		}
	}

	private Publication getPublication(IntelectualEntity entity) {
		try {
			return readService.publicationByIntEntId(entity.getId());
		} catch (Throwable e) {
			log("error getting publication for ie" + entity.getId());
			return null;
		}
	}

	private SourceDocument getSourceDocument(IntelectualEntity entity) {
		try {
			return readService.sourceDocumentByIntEntId(entity.getId());
		} catch (Throwable e) {
			log("error getting source document for ie" + entity.getId());
			return null;
		}
	}
}
