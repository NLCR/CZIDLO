package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StaticContentServiceAsync {

	void getTabRulesContent(AsyncCallback<String> callback);

	void getTabInfoContent(AsyncCallback<String> callback);

}
