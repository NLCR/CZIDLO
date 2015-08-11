package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

public class DtoToDigitalLibraryTransformer {

	private final DigitalLibraryDTO dto;

	public DtoToDigitalLibraryTransformer(DigitalLibraryDTO dto) {
		this.dto = dto;
	}

	public DigitalLibrary transform() {
		DigitalLibrary result = new DigitalLibrary();
		result.setId(dto.getId());
		result.setRegistrarId(dto.getRegistrarId());
		result.setName(dto.getName());
		result.setDescription(dto.getDescription());
		result.setUrl(dto.getUrl());
		return result;
	}
}
