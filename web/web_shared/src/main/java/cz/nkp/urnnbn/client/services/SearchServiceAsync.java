package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public interface SearchServiceAsync {

	// void getSearchResults(String request, AsyncCallback<ArrayList<IntelectualEntityDTO>> callback);

	void searchByUrnNbn(String request, AsyncCallback<IntelectualEntityDTO> callback);

	void getIntEntIdentifiersBySearch(String request, AsyncCallback<ArrayList<Long>> callback);

	void getIntelectualEntities(ArrayList<Long> identifiers, AsyncCallback<ArrayList<IntelectualEntityDTO>> callback);

	void getIntelectualEntity(Long intEntId, AsyncCallback<IntelectualEntityDTO> callback);
}
