package cz.nkp.urnnbn.server.dtoTransformation.entities;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.OtherEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalIssueDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorType;
import cz.nkp.urnnbn.shared.dto.ie.SourceDocumentDTO;
import cz.nkp.urnnbn.shared.dto.ie.ThesisDTO;

public class DtotoIntelectualEntityTransformer {

	private IntelectualEntity entity;
	private List<IntEntIdentifier> identifiers = new ArrayList<IntEntIdentifier>();
	private Originator originator;
	private Publication publication;
	private SourceDocument srcDoc;

	public DtotoIntelectualEntityTransformer(IntelectualEntityDTO entity) {
		transformEntityAndIdentifiers(entity);
		if (entity.getPrimaryOriginator() != null) {
			originator = transformOriginator(entity.getPrimaryOriginator(), entity.getId());
		}
		if (entity.getPublication() != null) {
			publication = transformPublication(entity.getPublication(), entity.getId());
		}
		if (entity.getSourceDocument() != null) {
			srcDoc = transformSrcDoc(entity.getSourceDocument(), entity.getId());
		}
	}

	private void transformEntityAndIdentifiers(IntelectualEntityDTO original) {
		if (original instanceof AnalyticalDTO) {
			entity = transformAnalytical((AnalyticalDTO) original);
		} else if (original instanceof MonographDTO) {
			entity = transformMonograph((MonographDTO) original);
		} else if (original instanceof MonographVolumeDTO) {
			entity = transformMonographVol((MonographVolumeDTO) original);
		} else if (original instanceof PeriodicalDTO) {
			entity = transformPeriodical((PeriodicalDTO) original);
		} else if (original instanceof PeriodicalVolumeDTO) {
			entity = transformPeriodicalVol((PeriodicalVolumeDTO) original);
		} else if (original instanceof PeriodicalIssueDTO) {
			entity = transformPeriodicalIssue((PeriodicalIssueDTO) original);
		} else if (original instanceof ThesisDTO) {
			entity = transformThesis((ThesisDTO) original);
		} else if (original instanceof OtherEntityDTO) {
			entity = transformOtherEntity((OtherEntityDTO) original);
		}
	}

	private IntelectualEntity transformOtherEntity(OtherEntityDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.THESIS);
		if (dto.getTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getTitle(), dto.getId()));
		}
		if (dto.getSubTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.SUB_TITLE, dto.getSubTitle(), dto.getId()));
		}
		if (dto.getIsbn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISBN, dto.getIsbn(), dto.getId()));
		}
		if (dto.getIssn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSN, dto.getIssn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformThesis(ThesisDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.THESIS);
		result.setDegreeAwardingInstitution(dto.getDegreeAwardingInstitution());
		if (dto.getTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getTitle(), dto.getId()));
		}
		if (dto.getSubTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.SUB_TITLE, dto.getSubTitle(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformPeriodicalIssue(PeriodicalIssueDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.PERIODICAL_ISSUE);
		if (dto.getPeriodicalTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getPeriodicalTitle(), dto.getId()));
		}
		if (dto.getVolumeTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.VOLUME_TITLE, dto.getVolumeTitle(), dto.getId()));
		}
		if (dto.getIssueTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSUE_TITLE, dto.getIssueTitle(), dto.getId()));
		}
		if (dto.getIssn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSN, dto.getIssn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformPeriodicalVol(PeriodicalVolumeDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.PERIODICAL_VOLUME);
		if (dto.getPeriodicalTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getPeriodicalTitle(), dto.getId()));
		}
		if (dto.getVolumeTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.VOLUME_TITLE, dto.getVolumeTitle(), dto.getId()));
		}
		if (dto.getIssn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSN, dto.getIssn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformPeriodical(PeriodicalDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.PERIODICAL);
		if (dto.getTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getTitle(), dto.getId()));
		}
		if (dto.getSubTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.SUB_TITLE, dto.getSubTitle(), dto.getId()));
		}
		if (dto.getIssn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSN, dto.getIssn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformMonographVol(MonographVolumeDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.MONOGRAPH_VOLUME);
		if (dto.getMonographTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getMonographTitle(), dto.getId()));
		}
		if (dto.getVolumeTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.VOLUME_TITLE, dto.getVolumeTitle(), dto.getId()));
		}
		if (dto.getIsbn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISBN, dto.getIsbn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformMonograph(MonographDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.MONOGRAPH);
		if (dto.getTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getTitle(), dto.getId()));
		}
		if (dto.getSubTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.SUB_TITLE, dto.getSubTitle(), dto.getId()));
		}
		if (dto.getIsbn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISBN, dto.getIsbn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntelectualEntity transformAnalytical(AnalyticalDTO dto) {
		IntelectualEntity result = new IntelectualEntity();
		setCommonAttributes(dto, result);
		result.setEntityType(EntityType.ANALYTICAL);
		if (dto.getTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.TITLE, dto.getTitle(), dto.getId()));
		}
		if (dto.getSubTitle() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.SUB_TITLE, dto.getSubTitle(), dto.getId()));
		}
		if (dto.getIsbn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISBN, dto.getIsbn(), dto.getId()));
		}
		if (dto.getIssn() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.ISSN, dto.getIssn(), dto.getId()));
		}
		if (dto.getCcnb() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.CCNB, dto.getCcnb(), dto.getId()));
		}
		if (dto.getOtherId() != null) {
			identifiers.add(buildIdentifier(IntEntIdType.OTHER, dto.getOtherId(), dto.getId()));
		}
		return result;
	}

	private IntEntIdentifier buildIdentifier(IntEntIdType type, String value, Long entityId) {
		IntEntIdentifier id = new IntEntIdentifier();
		id.setIntEntDbId(entityId);
		id.setType(type);
		id.setValue(value);
		return id;
	}

	private void setCommonAttributes(IntelectualEntityDTO dto, IntelectualEntity result) {
		result.setId(dto.getId());
		result.setDocumentType(dto.getDocumentType());
		result.setDigitalBorn(dto.isDigitalBorn());
		result.setOtherOriginator(dto.getOtherOriginator());
	}

	private Originator transformOriginator(PrimaryOriginatorDTO original, Long entityId) {
		Originator result = new Originator();
		result.setId(entityId);
		result.setType(toOriginatorType(original.getType()));
		result.setValue(original.getValue());
		return result;
	}

	private OriginType toOriginatorType(PrimaryOriginatorType type) {
		switch (type) {
		case AUTHOR:
			return OriginType.AUTHOR;
		case CORPORATION:
			return OriginType.CORPORATION;
		case EVENT:
			return OriginType.EVENT;
		default:
			return null;
		}
	}

	private Publication transformPublication(PublicationDTO original, Long id) {
		Publication result = new Publication();
		result.setId(id);
		result.setPublisher(original.getPublisher());
		result.setPlace(original.getPublicationPlace());
		result.setYear(original.getPublicationYear());
		return result;
	}

	private SourceDocument transformSrcDoc(SourceDocumentDTO original, Long id) {
		SourceDocument result = new SourceDocument();
		result.setId(id);
		result.setTitle(original.getTitle());
		result.setVolumeTitle(original.getVolumeTitle());
		result.setIssueTitle(original.getIssueTitle());
		result.setCcnb(original.getCcnb());
		result.setIsbn(original.getIsbn());
		result.setIssn(original.getIssn());
		result.setOtherId(original.getOtherId());
		PublicationDTO publication = original.getPublication();
		if (publication != null) {
			result.setPublisher(publication.getPublisher());
			result.setPublicationPlace(publication.getPublicationPlace());
			result.setPublicationYear(publication.getPublicationYear());
		}
		return result;
	}

	public IntelectualEntity getEntity() {
		return entity;
	}

	public List<IntEntIdentifier> getIdentifiers() {
		return identifiers;
	}

	public Originator getOriginator() {
		return originator;
	}

	public Publication getPublication() {
		return publication;
	}

	public SourceDocument getSrcDoc() {
		return srcDoc;
	}
}
