package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public interface AuthServiceAsync {

    void getLoggedUser(AsyncCallback<UserDTO> callback);

    void checkPasswordMatch(String login, String password, AsyncCallback<Boolean> callback);

    void changePassword(String login, String newPassword, AsyncCallback<Void> callback);

}
