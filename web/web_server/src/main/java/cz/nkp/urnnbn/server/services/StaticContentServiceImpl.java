package cz.nkp.urnnbn.server.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.server.dtoTransformation.ContentDtoTransformer;
import cz.nkp.urnnbn.shared.dto.ContentDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class StaticContentServiceImpl extends AbstractService implements StaticContentService {

	private static final long serialVersionUID = -507074829836983767L;
	private static final Logger logger = Logger.getLogger(StaticContentServiceImpl.class.getName());

	@Override
	public ContentDTO getContentByNameAndLanguage(String name, String language) throws ServerException {
		try {
			Content content = this.readService.contentByNameAndLanguage(name, language);
			ContentDtoTransformer transformer = new ContentDtoTransformer(content);
			return transformer.transform();
		} catch (Throwable e) {
			logger.log(Level.SEVERE, null, e);
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public void update(ContentDTO content) throws ServerException {
		try {
			checkNotReadOnlyMode();
			Content result = new Content();
			result.setId(content.getId());
			result.setLanguage(content.getLanguage());
			result.setName(content.getName());
			result.setContent(content.getContent());
			this.updateService.updateContent(result, getUserLogin());
		} catch (Throwable e) {
			logger.log(Level.SEVERE, null, e);
			throw new ServerException(e.getMessage());
		}
	}

}
