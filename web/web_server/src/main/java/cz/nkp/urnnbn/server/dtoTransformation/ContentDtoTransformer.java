package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.shared.dto.ContentDTO;

public class ContentDtoTransformer extends DtoTransformer {

    private final Content content;

    public ContentDtoTransformer(Content content) {
        this.content = content;
    }

    @Override
    public ContentDTO transform() {
        ContentDTO result = new ContentDTO();
        result.setId(content.getId());
        result.setLanguage(content.getLanguage());
        result.setName(content.getName());
        result.setContent(content.getContent());
        return result;
    }

}
