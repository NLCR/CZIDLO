package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public interface UserAccountServiceAsync {

	void getAllUsers(AsyncCallback<ArrayList<UserDTO>> callback);

	void insertUser(UserDTO user, AsyncCallback<UserDTO> callback);

	void insertRegistrarRight(long userId, long registrarId, AsyncCallback<Void> callback);

	void updateUser(UserDTO user, AsyncCallback<Void> callback);

	void registrarsManagedByUser(Long userId, AsyncCallback<ArrayList<RegistrarDTO>> callback);

	void deleteUser(Long userId, AsyncCallback<Void> callback);

	void deleteRegistrarRight(long userId, long registrarId, AsyncCallback<Void> callback);

}
