package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import cz.nkp.urnnbn.shared.SearchResult;

public interface SearchServiceAsync {

    void search(String request, long start, int rows, AsyncCallback<SearchResult> callback);
}
