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
    private static final int MIN_SEARCH_STRING_LENGTH = 3;
    private static final int MAX_SEARCH_STRING_LENGTH = 100;
    private static ArrayList<Long> EMPTY_LONG_LIST = new ArrayList<Long>(0);
    private static ArrayList<DigitalInstanceDTO> EMPTY_DI_LIST = new ArrayList<DigitalInstanceDTO>(0);
    private static final int FULLTEXT_SEARCH_HARD_LIMIT = 100;

    @Override
    public ArrayList<Long> getIntEntIdentifiersBySearch(String query) throws ServerException {
        try {
            // logger.info("searching for \"" + query + "\" (ie identifiers)");
            if (query == null || query.isEmpty()) {
                return EMPTY_LONG_LIST;
            } else if (query.length() > MAX_SEARCH_STRING_LENGTH) {
                return searchByIdentifiers(query.substring(0, MAX_SEARCH_STRING_LENGTH));
            } else {
                return searchByIdentifiers(query);
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ArrayList<IntelectualEntityDTO> getIntelectualEntities(ArrayList<Long> identifiers) throws ServerException {
        try {
            ArrayList<IntelectualEntityDTO> result = new ArrayList<IntelectualEntityDTO>(identifiers.size());
            for (Long id : identifiers) {
                IntelectualEntity entity = readService.entityById(id);
                result.add(transformedEntity(entity));
            }
            return result;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public IntelectualEntityDTO getIntelectualEntity(Long intEntId) throws ServerException {
        try {
            IntelectualEntity entity = readService.entityById(intEntId);
            return transformedEntity(entity);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public IntelectualEntityDTO searchByUrnNbn(String query) throws ServerException {
        // logger.info("searching for \"" + query + "\" (urn:nbn)");
        try {
            UrnNbn urnNbn;
            try {
                urnNbn = UrnNbn.valueOf(query);
            } catch (Throwable e) {
                return null;
            }
            UrnNbnWithStatus urnFetched = readService.urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
            if (urnFetched.getStatus() == Status.ACTIVE || urnFetched.getStatus() == Status.DEACTIVATED) {
                DigitalDocument digDoc = readService.digDocByInternalId(urnFetched.getUrn().getDigDocId());
                Set<Long> result = new HashSet<Long>();
                result.add(digDoc.getIntEntId());
                return transformedEntity(readService.entityById(digDoc.getIntEntId()));
            } else {
                return null;
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<Long> searchByIdentifiers(String query) {
        // logger.info("searching in ie identifiers");
        query = query.toLowerCase();
        // logger.info("query (toLowerCase): " + query);
        query = normalizeIfIsbn(query);
        // logger.info("query (normalizeIfIsbn): " + query);
        query = normalizeIfIssn(query);
        // logger.info("query (normalizeIfIssn): " + query);
        query = normalizeIfCcnb(query);
        // logger.info("query (normalizeIfCcnb): " + query);
        query = replaceForbiddenCharacters(query, new char[] { '\'', ':', '!', '&' });
        // logger.info("query (replaceForbiddenCharacters): " + query);
        String[] words = query.split(" ");
        // logger.info("query (split): " + toString(words));
        StringBuilder indexSearchQuery = new StringBuilder();
        indexSearchQuery.append('\'');
        boolean atLeastOneAcceptedToken = false;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String trimmed = word.trim();
            // logger.info("word: " + word + ", trimmed: " + trimmed);
            if (!trimmed.isEmpty() && (isNumber(trimmed) || trimmed.length() >= MIN_SEARCH_STRING_LENGTH)) {
                indexSearchQuery.append(trimmed);
                atLeastOneAcceptedToken = true;
                if (i != words.length - 1) {
                    indexSearchQuery.append('&');
                }
            }
        }
        indexSearchQuery.append('\'');
        String indexSearchQueryStr = indexSearchQuery.toString();
        // logger.info("indexSearchQuery: " + indexSearchQueryStr);
        if (atLeastOneAcceptedToken) {
            return new ArrayList<Long>(readService.intEntIdsByFulltextSearch(indexSearchQueryStr, FULLTEXT_SEARCH_HARD_LIMIT));
        } else {
            return new ArrayList<Long>();
        }
    }

    private boolean isNumber(String trimmed) {
        try {
            Integer.valueOf(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String toString(String[] words) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < words.length; i++) {
            builder.append(words[i]);
            if (i < words.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    private String normalizeIfCcnb(String query) {
        // query = query.toUpperCase();
        String standardPrefix = "cnb";
        String incorrectPrefix = "čnb";
        if (query.toUpperCase().startsWith(incorrectPrefix)) {
            String normalized = standardPrefix + query.substring(incorrectPrefix.length());
            // logger.info("CCNB normalized: '" + normalized + "'");
            return normalized;
        } else {
            return query;
        }
    }

    private String normalizeIfIssn(String query) {
        // query = query.toUpperCase();
        String[] preficies = new String[] { "issn ", "issn: ", "issn:" };
        for (String prefix : preficies) {
            if (query.startsWith(prefix)) {
                String normalized = query.substring(prefix.length());
                // logger.info("ISSN normalized: '" + normalized + "'");
                return normalized;
            }
        }
        return query;
    }

    private String normalizeIfIsbn(String query) {
        // query = query.toUpperCase();
        String[] preficies = new String[] { "isbn:", "isbn: ", "isbn:" };
        for (String prefix : preficies) {
            if (query.startsWith(prefix)) {
                query = query.substring(prefix.length());
                String normalized = removeSeparators(query, new char[] { '-', ' ' });
                // logger.info("ISBN normalized: '" + normalized + "'");
                return normalized;
            }
        }
        return query;
    }

    private String removeSeparators(String original, char[] separators) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!isSeparator(character, separators)) {
                result.append(character);
            }
        }
        return result.toString();
    }

    private boolean isSeparator(char character, char[] separators) {
        for (char separator : separators) {
            if (character == separator) {
                return true;
            }
        }
        return false;
    }

    private String replaceForbiddenCharacters(String string, char[] forbiddenChars) {
        for (char forbiddenChar : forbiddenChars) {
            string = string.replace(forbiddenChar, ' ');
        }
        return string;
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