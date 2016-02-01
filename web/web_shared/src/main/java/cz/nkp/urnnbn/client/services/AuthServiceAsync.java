package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.UserDTO;

public interface AuthServiceAsync {

    void getLoggedUser(AsyncCallback<UserDTO> callback);

}
