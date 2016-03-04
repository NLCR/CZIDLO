package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {

    public IntelectualEntityDTO searchByUrnNbn(String request) throws ServerException;

    ArrayList<Long> searchMetadata(String request) throws ServerException;

    ArrayList<IntelectualEntityDTO> getIntelectualEntities(ArrayList<Long> identifiers) throws ServerException;

    IntelectualEntityDTO getIntelectualEntity(Long intEntId) throws ServerException;
}
