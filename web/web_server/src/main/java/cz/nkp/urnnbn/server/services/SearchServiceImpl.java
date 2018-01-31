package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.SearchResult;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;
import cz.nkp.urnnbn.solr_indexer.SolrConnector;
import cz.nkp.urnnbn.solr_indexer.SolrUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchServiceImpl extends AbstractService implements SearchService {

    private static final long serialVersionUID = 1750995108864579331L;
    private static final Logger LOGGER = Logger.getLogger(SearchServiceImpl.class.getName());
    private static final ArrayList<DigitalInstanceDTO> EMPTY_DI_LIST = new ArrayList<>(0);
    private static final int MAX_QUERY_TOKENS = 20;


    @Override
    public SearchResult search(String query, long start, int rows) throws ServerException {
        try {
            SolrConnector solrConnector = new SolrConnector(
                    WebModuleConfiguration.instanceOf().getSolrBaseUrl(),
                    WebModuleConfiguration.instanceOf().getSolrCollection(),
                    WebModuleConfiguration.instanceOf().getSolrUseHttps());
            String urnNbnField = "dd.id";
            String queryRefined = refineQuery(query);
            SolrDocumentList docList = solrConnector.searchInAllFields(queryRefined, start, rows, urnNbnField);
            return toSearchResults(docList, urnNbnField);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private String refineQuery(String query) {
        String[] tokens = query.split("\\s+");
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (String token : tokens) {
            if (++counter < MAX_QUERY_TOKENS) {
                if (counter > 1) {
                    builder.append(" || ");
                }
                if (isUrnNbn(token)) {
                    builder.append("\"").append(token).append("\"");
                } else {
                    //ordinary token
                    // TODO: 31.1.18 possibly handle specially looking tokens like isbn, issn
                    // TODO: 31.1.18 possibly prefer title over other fields
                    builder.append(SolrUtils.escapeSolrSpecialChars(token));
                }
            }
        }
        String refined = builder.toString();
        /*LOGGER.info("query: " + query);
        LOGGER.info("refined: " + refined);*/
        return refined;
    }

    private boolean isUrnNbn(String string) {
        try {
            UrnNbn.valueOf(string);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    private SearchResult toSearchResults(SolrDocumentList docList, String urnNbnField) {
        SearchResult result = new SearchResult();
        result.setNumFound(docList.getNumFound());
        result.setStart(docList.getStart());
        ArrayList<IntelectualEntityDTO> ieList = new ArrayList<>();
        for (int i = 0; i < docList.size(); i++) {
            SolrDocument solrDocument = docList.get(i);
            UrnNbn urnNbn = UrnNbn.valueOf((String) solrDocument.getFieldValue(urnNbnField));
            IntelectualEntityDTO intelectualEntityDTO = urnNbnToIntelectualEntityDTO(urnNbn);
            if (intelectualEntityDTO != null) {
                ieList.add(intelectualEntityDTO);
            }
        }
        result.setIntelectualEntities(ieList);
        return result;
    }

    private IntelectualEntityDTO urnNbnToIntelectualEntityDTO(UrnNbn urnNbn) {
        UrnNbnWithStatus urnFetched = readService.urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
        if (urnFetched.getStatus() == Status.ACTIVE || urnFetched.getStatus() == Status.DEACTIVATED) {
            DigitalDocument digDoc = readService.digDocByInternalId(urnFetched.getUrn().getDigDocId());
            //Set<Long> result = new HashSet<>();
            //result.add(digDoc.getIntEntId());
            return transformedEntity(readService.entityById(digDoc.getIntEntId()));
        } else {
            return null;
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
            return Collections.<RegistrarScopeIdentifier>emptyList();
        }
    }

    private List<DigitalDocument> getDigitalDocuments(IntelectualEntity entity) {
        try {
            return readService.digDocsOfIntEnt(entity.getId());
        } catch (Throwable e) {
            log("error getting digital documents for ie" + entity.getId());
            return Collections.<DigitalDocument>emptyList();
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
            return Collections.<IntEntIdentifier>emptyList();
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
