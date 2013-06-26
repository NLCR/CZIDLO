package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.ContentDTO;

@RemoteServiceRelativePath("staticContent")
public interface StaticContentService extends RemoteService {

	public ContentDTO getContentByNameAndLanguage(String language, String name);
	
	public void update(ContentDTO content);
	
	public String getTabRulesContent();
	
	public String getTabInfoContent();
}
