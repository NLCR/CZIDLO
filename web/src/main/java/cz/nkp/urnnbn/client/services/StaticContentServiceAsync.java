package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.shared.dto.ContentDTO;

public interface StaticContentServiceAsync {

	void getContentByNameAndLanguage(String language, String name, AsyncCallback<ContentDTO> callback);
	
	void update(ContentDTO content, AsyncCallback<Void> callback);
	
	void getTabRulesContent(AsyncCallback<String> callback);

	void getTabInfoContent(AsyncCallback<String> callback);

}
