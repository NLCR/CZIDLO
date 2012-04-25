package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class ArchiverDtoTransformer extends DtoTransformer {

	private final Archiver original;

	public ArchiverDtoTransformer(Archiver archiver) {
		this.original = archiver;
	}

	@Override
	public ArchiverDTO transform() {
		ArchiverDTO result = new ArchiverDTO();
		result.setId(original.getId());
		result.setCreated(dateTimeToStringOrNull(original.getCreated()));
		result.setModified(dateTimeToStringOrNull(original.getModified()));
		result.setName(original.getName());
		result.setDescription(original.getDescription());
		return result;
	}
}
