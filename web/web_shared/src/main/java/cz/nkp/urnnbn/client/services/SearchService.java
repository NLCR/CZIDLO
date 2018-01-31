package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import cz.nkp.urnnbn.shared.SearchResult;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {

    SearchResult search(String request, long start, int rows) throws ServerException;

}
