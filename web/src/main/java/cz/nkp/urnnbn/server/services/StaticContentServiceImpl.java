package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;

public class StaticContentServiceImpl extends AbstractService implements StaticContentService{

	private static final long serialVersionUID = -507074829836983767L;

	@Override
	public String getTabRulesContent() {
		return this.readService.getContentByNameAndLanguage("rules", "cz");
		//return WebModuleConfiguration.instanceOf().getRulesTabContent();
	}

	@Override
	public String getTabInfoContent() {
		return this.readService.getContentByNameAndLanguage("info", "cz");
		//return WebModuleConfiguration.instanceOf().getInfoTabContent();
	}

}