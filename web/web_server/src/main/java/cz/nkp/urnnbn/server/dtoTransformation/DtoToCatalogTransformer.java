package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;

public class DtoToCatalogTransformer {

    private final CatalogDTO original;

    public DtoToCatalogTransformer(CatalogDTO original) {
        this.original = original;
    }

    public Catalog transform() {
        Catalog result = new Catalog();
        result.setId(original.getId());
        result.setRegistrarId(original.getRegistrarId());
        result.setName(original.getName());
        result.setDescription(original.getDescription());
        result.setUrlPrefix(original.getUrlPrefix());
        return result;
    }
}
