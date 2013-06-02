package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StaticContentServiceAsync {

	void getContentByNameAndLanguage(String language, String name, AsyncCallback<String> callback);
	
	void setContentByNameAndLanguage(String language, String name, String content, AsyncCallback<Void> callback);
	
	void getTabRulesContent(AsyncCallback<String> callback);

	void getTabInfoContent(AsyncCallback<String> callback);

}
