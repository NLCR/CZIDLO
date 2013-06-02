package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("staticContent")
public interface StaticContentService extends RemoteService {

	public String getContentByNameAndLanguage(String language, String name);
	
	public void setContentByNameAndLanguage(String language, String name, String content);
	
	public String getTabRulesContent();
	
	public String getTabInfoContent();
}
