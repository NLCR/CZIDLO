package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public class DigitalInstanceDtoTransformer extends DtoTransformer{
	
	private final DigitalInstance instance;
	private final DigitalLibrary library;

	public DigitalInstanceDtoTransformer(DigitalInstance instance, DigitalLibrary library) {
		this.instance = instance;
		this.library = library;
	}

	public DigitalInstanceDTO transform() {
		DigitalInstanceDTO result = new DigitalInstanceDTO();
		result.setAccessibility(instance.getAccessibility());
		result.setFormat(instance.getFormat());
		result.setId(instance.getId());
		result.setCreated(dateTimeToStringOrNull(instance.getCreated()));
		result.setModified(dateTimeToStringOrNull(instance.getModified()));
		result.setUrl(instance.getUrl());
		result.setActive(instance.isActive());
		result.setLibrary(new DigitalLibraryDtoTransformer(library).transform());
		return result;
	}



}
