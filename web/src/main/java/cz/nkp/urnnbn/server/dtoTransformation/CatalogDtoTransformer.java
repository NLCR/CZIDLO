package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;

public class CatalogDtoTransformer extends DtoTransformer {

	private final Catalog catalog;

	public CatalogDtoTransformer(Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public CatalogDTO transform() {
		CatalogDTO result = new CatalogDTO();
		result.setId(catalog.getId());
		result.setRegistrarId(catalog.getRegistrarId());
		result.setCreated(dateTimeToStringOrNull(catalog.getCreated()));
		result.setModified(dateTimeToStringOrNull(catalog.getModified()));
		result.setName(catalog.getName());
		result.setDescription(catalog.getDescription());
		result.setUrlPrefix(catalog.getUrlPrefix());
		return result;
	}
}
