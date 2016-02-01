package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("auth")
public interface AuthService extends RemoteService {
    UserDTO getLoggedUser() throws ServerException;
}
