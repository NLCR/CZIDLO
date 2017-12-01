package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.v5.exceptions.InvalidDataException;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv5.unmarshallers.RecordImportUnmarshaller;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EmptyDigDocAndIeFieldFiller {

    private static final Logger LOGGER = Logger.getLogger(EmptyDigDocAndIeFieldFiller.class.getName());

    private final UrnNbn urnNbn;
    private final String login;

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    public EmptyDigDocAndIeFieldFiller(UrnNbn urnNbn, String login) {
        this.urnNbn = urnNbn;
        this.login = login;
    }

    public boolean update(RecordImportUnmarshaller unmarshaller, ResponseFormat format) throws UnknownUserException, AccessException,
            UnknownDigDocException, UnknownIntelectualEntity {
        boolean modified = false;
        DateTime now = new DateTime();
        DigitalDocument digDocFromDb = dataAccessService().digDocByInternalId(urnNbn.getDigDocId());

        // intelectual entity
        IntelectualEntity intEntFromDb = dataAccessService().entityById(digDocFromDb.getIntEntId());
        if (intEntFromDb.getEntityType() != unmarshaller.getIntelectualEntity().getEntityType()) {
            throw new InvalidDataException(format, "intelectual entity must be " + intEntFromDb.getEntityType());
        }
        IntelectualEntity intEntUpdatedIfChanged = mergeIntEnt(intEntFromDb, unmarshaller.getIntelectualEntity());
        Long ieId = intEntFromDb.getId();
        Publication publicationFromDb = dataAccessService().publicationByIntEntId(ieId);
        Publication publicationUpdatedIfChanged = mergePublication(ieId, publicationFromDb, unmarshaller.getPublication());
        Originator originatorFromDb = dataAccessService().originatorByIntEntId(ieId);
        Originator orignatorUpdatedIfMerged = mergeOriginator(ieId, originatorFromDb, unmarshaller.getOriginator());
        SourceDocument srcDocFromDb = dataAccessService().sourceDocumentByIntEntId(ieId);
        SourceDocument srcDocUpdatedIfMerged = mergeSrcDoc(ieId, srcDocFromDb, unmarshaller.getSourceDocument());
        List<IntEntIdentifier> ieIdsFromDb = dataAccessService().intEntIdentifiersByIntEntId(ieId);
        List<IntEntIdentifier> ieIdsUpdatedIfMerged = mergeIeIds(ieIdsFromDb, unmarshaller.getIntEntIdentifiers(), intEntFromDb.getId());
        // LOGGER.info("from db: " + toString(ieIdsFromDb));
        // LOGGER.info("merged: " + toString(ieIdsUpdatedIfMerged));

        if (intEntUpdatedIfChanged != null || publicationUpdatedIfChanged != null || orignatorUpdatedIfMerged != null
                || srcDocUpdatedIfMerged != null || ieIdsUpdatedIfMerged != null) {
            IntelectualEntity entity = intEntUpdatedIfChanged != null ? intEntUpdatedIfChanged : intEntFromDb;
            Publication publication = publicationUpdatedIfChanged != null ? publicationUpdatedIfChanged : publicationFromDb;
            Originator originator = orignatorUpdatedIfMerged != null ? orignatorUpdatedIfMerged : originatorFromDb;
            SourceDocument srcDoc = srcDocUpdatedIfMerged != null ? srcDocUpdatedIfMerged : srcDocFromDb;
            List<IntEntIdentifier> ieIds = ieIdsUpdatedIfMerged != null ? ieIdsUpdatedIfMerged : ieIdsFromDb;
            entity.setModified(now);
            dataUpdateService().updateIntelectualEntity(entity, originator, publication, srcDoc, ieIds, login);
            modified = true;
            // LOGGER.info("updating ie");
        } else {
            // LOGGER.info("not updating ie");
        }

        // digital document
        DigitalDocument digDocMergedIfChanged = mergeDigDoc(digDocFromDb, unmarshaller.getDigitalDocument());
        if (digDocMergedIfChanged != null) {
            digDocMergedIfChanged.setModified(now);
            dataUpdateService().updateDigitalDocument(digDocMergedIfChanged, login);
            modified = true;
            // LOGGER.info("updating dd");
        } else {
            // LOGGER.info("not updating dd");
        }

        return modified;
    }

    private String toString(List<IntEntIdentifier> ids) {
        StringBuilder builder = new StringBuilder();
        if (ids != null) {
            for (IntEntIdentifier id : ids) {
                builder.append(id.getType().toString()).append(":").append(id.getValue());
                builder.append(",");
            }
            return builder.toString();
        } else {
            return null;
        }
    }

    private DigitalDocument mergeDigDoc(DigitalDocument fromDb, DigitalDocument fromRequest) {
        if (fromRequest == null) {
            return null;
        } else {
            DigitalDocument merged = new DigitalDocument(fromDb);
            boolean modified = false;
            if (fromDb.getColorDepth() == null && fromRequest.getColorDepth() != null) {
                merged.setColorDepth(fromRequest.getColorDepth());
                modified = true;
            }
            if (fromDb.getColorModel() == null && fromRequest.getColorModel() != null) {
                merged.setColorModel(fromRequest.getColorModel());
                modified = true;
            }
            if (fromDb.getCompression() == null && fromRequest.getCompression() != null) {
                merged.setCompression(fromRequest.getCompression());
                modified = true;
            }
            if (fromDb.getCompressionRatio() == null && fromRequest.getCompressionRatio() != null) {
                merged.setCompressionRatio(fromRequest.getCompressionRatio());
                modified = true;
            }
            if (fromDb.getContractNumber() == null && fromRequest.getContractNumber() != null) {
                merged.setContractNumber(fromRequest.getContractNumber());
                modified = true;
            }
            if (fromDb.getExtent() == null && fromRequest.getExtent() != null) {
                merged.setExtent(fromRequest.getExtent());
                modified = true;
            }
            if (fromDb.getFinancedFrom() == null && fromRequest.getFinancedFrom() != null) {
                merged.setFinancedFrom(fromRequest.getFinancedFrom());
                modified = true;
            }
            if (fromDb.getFormat() == null && fromRequest.getFormat() != null) {
                merged.setFormat(fromRequest.getFormat());
                modified = true;
            }
            if (fromDb.getFormatVersion() == null && fromRequest.getFormatVersion() != null) {
                merged.setFormatVersion(fromRequest.getFormatVersion());
                modified = true;
            }
            if (fromDb.getIccProfile() == null && fromRequest.getIccProfile() != null) {
                merged.setIccProfile(fromRequest.getIccProfile());
                modified = true;
            }
            if (fromDb.getPictureHeight() == null && fromRequest.getPictureHeight() != null) {
                merged.setPictureHeight(fromRequest.getPictureHeight());
                modified = true;
            }
            if (fromDb.getPictureWidth() == null && fromRequest.getPictureWidth() != null) {
                merged.setPictureWidth(fromRequest.getPictureWidth());
                modified = true;
            }
            if (fromDb.getResolutionHorizontal() == null && fromRequest.getResolutionHorizontal() != null) {
                merged.setResolutionHorizontal(fromRequest.getResolutionHorizontal());
                modified = true;
            }
            if (fromDb.getResolutionVertical() == null && fromRequest.getResolutionVertical() != null) {
                merged.setResolutionVertical(fromRequest.getResolutionVertical());
                modified = true;
            }
            if (modified) {
                merged.setModified(new DateTime());
            }
            return modified ? merged : null;
        }
    }

    private IntelectualEntity mergeIntEnt(IntelectualEntity fromDb, IntelectualEntity fromRequest) {
        if (fromRequest == null) {
            return null;
        } else {
            boolean modified = false;
            IntelectualEntity merged = new IntelectualEntity(fromDb);
            if (fromDb.getDegreeAwardingInstitution() == null && fromRequest.getDegreeAwardingInstitution() != null) {
                merged.setDegreeAwardingInstitution(fromRequest.getDegreeAwardingInstitution());
                modified = true;
            }
            if (fromDb.getDocumentType() == null && fromRequest.getDocumentType() != null) {
                merged.setDocumentType(fromRequest.getDocumentType());
                modified = true;
            }
            if (fromDb.getOtherOriginator() == null && fromRequest.getOtherOriginator() != null) {
                merged.setOtherOriginator(fromRequest.getOtherOriginator());
                modified = true;
            }
            return modified ? merged : null;
        }
    }

    private List<IntEntIdentifier> mergeIeIds(List<IntEntIdentifier> fromDb, List<IntEntIdentifier> fromRequest, long ieId) {
        if (fromRequest.isEmpty()) {
            return null;
        } else {
            Map<IntEntIdType, String> map = new HashMap<>();
            for (IntEntIdentifier id : fromDb) {
                map.put(id.getType(), id.getValue());
            }
            boolean modified = false;
            for (IntEntIdentifier id : fromRequest) {
                if (!map.containsKey(id.getType())) {
                    // new id
                    map.put(id.getType(), id.getValue());
                    modified = true;
                }
            }
            if (modified) {
                List<IntEntIdentifier> merged = new ArrayList<>();
                for (IntEntIdType type : map.keySet()) {
                    IntEntIdentifier id = new IntEntIdentifier();
                    id.setType(type);
                    id.setValue(map.get(type));
                    id.setIntEntDbId(ieId);
                    merged.add(id);
                }
                return merged;
            } else {
                return null;
            }
        }
    }

    private SourceDocument mergeSrcDoc(Long ieId, SourceDocument fromDb, SourceDocument fromRequest) {
        if (fromDb != null && fromRequest != null) {
            // merge
            SourceDocument merged = new SourceDocument(fromDb);
            boolean modified = false;
            // title info
            if (fromDb.getTitle() == null && fromRequest.getTitle() != null) {
                merged.setTitle(fromRequest.getTitle());
                modified = true;
            }
            if (fromDb.getVolumeTitle() == null && fromRequest.getVolumeTitle() != null) {
                merged.setVolumeTitle(fromRequest.getVolumeTitle());
                modified = true;
            }
            if (fromDb.getIssueTitle() == null && fromRequest.getIssueTitle() != null) {
                merged.setIssueTitle(fromRequest.getIssueTitle());
                modified = true;
            }
            // ids
            if (fromDb.getCcnb() == null && fromRequest.getCcnb() != null) {
                merged.setCcnb(fromRequest.getCcnb());
                modified = true;
            }
            if (fromDb.getIsbn() == null && fromRequest.getIsbn() != null) {
                merged.setIsbn(fromRequest.getIsbn());
                modified = true;
            }
            if (fromDb.getIssn() == null && fromRequest.getIssn() != null) {
                merged.setIssn(fromRequest.getIssn());
                modified = true;
            }
            if (fromDb.getOtherId() == null && fromRequest.getOtherId() != null) {
                merged.setOtherId(fromRequest.getOtherId());
                modified = true;
            }
            // publication
            if (fromDb.getPublicationPlace() == null && fromRequest.getPublicationPlace() != null) {
                merged.setPublicationPlace(fromRequest.getPublicationPlace());
                modified = true;
            }
            if (fromDb.getPublicationYear() == null && fromRequest.getPublicationYear() != null) {
                merged.setPublicationYear(fromRequest.getPublicationYear());
                modified = true;
            }
            if (fromDb.getPublisher() == null && fromRequest.getPublisher() != null) {
                merged.setPublisher(fromRequest.getPublisher());
                modified = true;
            }
            return modified ? merged : null;
        } else if (fromRequest != null) {
            fromRequest.setId(ieId);
            return fromRequest;
        } else {
            return null;
        }
    }

    private Originator mergeOriginator(Long ieId, Originator fromDb, Originator fromRequest) {
        if (fromDb != null && fromRequest != null) {
            // merge
            return null;
        } else if (fromRequest != null) {
            fromRequest.setId(ieId);
            return fromRequest;
        } else {
            return null;
        }
    }

    private Publication mergePublication(Long ieId, Publication fromDb, Publication fromRequest) {
        if (fromDb != null && fromRequest != null) {
            // merge
            boolean modified = false;
            Publication merged = new Publication(fromDb);
            if (fromDb.getPlace() == null && fromRequest.getPlace() != null) {
                merged.setPlace(fromRequest.getPlace());
                modified = true;
            }
            if (fromDb.getPublisher() == null && fromRequest.getPublisher() != null) {
                merged.setPublisher(fromRequest.getPublisher());
                modified = true;
            }
            if (fromDb.getYear() == null && fromRequest.getYear() != null) {
                merged.setYear(fromRequest.getYear());
                modified = true;
            }
            return modified ? merged : null;
        } else if (fromRequest != null) {
            fromRequest.setId(ieId);
            return fromRequest;
        } else {
            return null;
        }
    }
}
