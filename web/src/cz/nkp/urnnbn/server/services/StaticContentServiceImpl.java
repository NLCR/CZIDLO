package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;

public class StaticContentServiceImpl extends AbstractService implements StaticContentService{

	private static final long serialVersionUID = -507074829836983767L;

	@Override
	public String getTabRulesContent() {
		return WebModuleConfiguration.instanceOf().getRulesTabContent();
	}

	@Override
	public String getTabInfoContent() {
		return WebModuleConfiguration.instanceOf().getInfoTabContent();
	}

}
