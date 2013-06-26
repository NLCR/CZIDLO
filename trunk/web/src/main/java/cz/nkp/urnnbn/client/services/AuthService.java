package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.UserDTO;

@RemoteServiceRelativePath("auth")
public interface AuthService extends RemoteService {
	UserDTO getActiveUser();
}
