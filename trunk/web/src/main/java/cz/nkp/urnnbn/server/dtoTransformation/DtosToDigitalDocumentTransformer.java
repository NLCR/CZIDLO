package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;

public class DtosToDigitalDocumentTransformer {

	private final DigitalDocumentDTO docDto;
	private final TechnicalMetadataDTO technicalMetadataDto;

	public DtosToDigitalDocumentTransformer(DigitalDocumentDTO docDto, TechnicalMetadataDTO technicalDto) {
		this.docDto = docDto;
		this.technicalMetadataDto = technicalDto;
	}

	public DigitalDocument transform() {
		DigitalDocument result = new DigitalDocument();
		if (docDto != null) {
			result.setId(docDto.getId());
			result.setIntEntId(docDto.getIntEntId());
			result.setRegistrarId(docDto.getRegistrar().getId());
			ArchiverDTO archiver = docDto.getArchiver();
			if (archiver != null) {
				result.setArchiverId(archiver.getId());
			}
			result.setFinancedFrom(docDto.getFinanced());
			result.setContractNumber(docDto.getContractNumber());
		}
		if (technicalMetadataDto != null) {
			result.setFormat(technicalMetadataDto.getFormat());
			result.setFormatVersion(technicalMetadataDto.getFormatVersion());
			result.setExtent(technicalMetadataDto.getExtent());
			result.setResolutionHorizontal(technicalMetadataDto.getResolutionHorizontal());
			result.setResolutionVertical(technicalMetadataDto.getResolutionVertical());
			result.setCompression(technicalMetadataDto.getCompression());
			result.setCompressionRatio(technicalMetadataDto.getCompressionRatio());
			result.setColorModel(technicalMetadataDto.getColorModel());
			result.setColorDepth(technicalMetadataDto.getColorDepth());
			result.setIccProfile(technicalMetadataDto.getIccProfile());
			result.setPictureHeight(technicalMetadataDto.getPicturHeight());
			result.setPictureWidth(technicalMetadataDto.getPictureWidth());
		}
		return result;
	}
}
