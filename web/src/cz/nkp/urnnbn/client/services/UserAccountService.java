package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("users")
public interface UserAccountService extends RemoteService {

	UserDTO insertUser(UserDTO user) throws ServerException;

	void insertRegistrarRight(long userId, long registrarId) throws ServerException;

	ArrayList<UserDTO> getAllUsers() throws ServerException;

	ArrayList<RegistrarDTO> registrarsManagedByUser(Long userId) throws ServerException;

	ArrayList<RegistrarDTO> registrarsManagedByUser() throws ServerException;

	void updateUser(UserDTO user) throws ServerException;

	void deleteUser(Long userId) throws ServerException;

	void deleteRegistrarRight(long userId, long registrarId) throws ServerException;

}
