package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import cz.nkp.urnnbn.shared.SearchResult;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

import java.util.ArrayList;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {


    // TODO: 31.1.18 remove unused methods

    @Deprecated
    public IntelectualEntityDTO searchByUrnNbn(String request) throws ServerException;

    @Deprecated
    ArrayList<Long> searchMetadata(String request) throws ServerException;

    @Deprecated
    ArrayList<IntelectualEntityDTO> getIntelectualEntities(ArrayList<Long> identifiers) throws ServerException;

    @Deprecated
    IntelectualEntityDTO getIntelectualEntity(Long intEntId) throws ServerException;

    SearchResult search(String request, long start, int rows) throws ServerException;

}
