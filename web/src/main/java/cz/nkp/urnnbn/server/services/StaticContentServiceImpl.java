package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.shared.dto.ContentDTO;
import cz.nkp.urnnbn.server.dtoTransformation.ContentDtoTransformer;

public class StaticContentServiceImpl extends AbstractService implements StaticContentService {

	private static final long serialVersionUID = -507074829836983767L;

	@Override
	public ContentDTO getContentByNameAndLanguage(String name, String language) {
		Content content = this.readService.getContentByNameAndLanguage(name, language);
		ContentDtoTransformer transformer = new ContentDtoTransformer(content);
		return transformer.transform();
	}
	
	@Override
	public void update(ContentDTO content) {
		Content result = new Content();
		result.setId(content.getId());
		result.setLanguage(content.getLanguage());
		result.setName(content.getName());
		result.setContent(content.getContent());
		this.updateService.updateContent(result);
	}
	
	@Override
	public String getTabRulesContent() {
		return this.readService.getContentByNameAndLanguage("rules", "cz").getContent();
		//return WebModuleConfiguration.instanceOf().getRulesTabContent();
	}

	@Override
	public String getTabInfoContent() {
		return this.readService.getContentByNameAndLanguage("info", "cz").getContent();
		//return WebModuleConfiguration.instanceOf().getInfoTabContent();
	}

}
