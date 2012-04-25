package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public interface SearchServiceAsync {

	void getSearchResults(String request,
			AsyncCallback<ArrayList<IntelectualEntityDTO>> callback);
}
