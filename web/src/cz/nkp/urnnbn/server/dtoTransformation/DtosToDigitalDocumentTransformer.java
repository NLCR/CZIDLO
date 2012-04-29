package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;

public class DtosToDigitalDocumentTransformer {

	private final DigitalDocumentDTO docDto;
	private final TechnicalMetadataDTO t;

	public DtosToDigitalDocumentTransformer(DigitalDocumentDTO docDto, TechnicalMetadataDTO technicalDto) {
		super();
		this.docDto = docDto;
		this.t = technicalDto;
	}

	public DigitalDocument transform() {
		DigitalDocument result = new DigitalDocument();
		if (docDto != null) {
			result.setId(docDto.getId());
			result.setRegistrarId(docDto.getRegistrar().getId());
			ArchiverDTO archiver = docDto.getArchiver();
			if (archiver != null) {
				result.setArchiverId(archiver.getId());
			}
			result.setFinancedFrom(docDto.getFinanced());
			result.setContractNumber(docDto.getContractNumber());
		}
		if (t != null) {
			result.setFormat(t.getFormat());
			result.setFormatVersion(t.getFormatVersion());
			result.setExtent(t.getExtent());
			result.setResolutionHorizontal(t.getResolutionHorizontal());
			result.setResolutionVertical(t.getResolutionVertical());
			result.setCompression(t.getCompression());
			result.setCompressionRatio(t.getCompressionRatio());
			result.setColorModel(t.getColorModel());
			result.setColorDepth(t.getColorDepth());
			result.setIccProfile(t.getIccProfile());
			result.setPictureHeight(t.getPicturHeight());
			result.setPictureWidth(t.getPictureWidth());
		}
		return result;
	}
}
