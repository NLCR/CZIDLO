/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface UserDAO {

    public String TABLE_NAME = "UserAccount";
    public String SEQ_NAME = "seq_UserAccount";
    public String ATTR_ID = "id";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_LOGIN = "login";
    public String ATTR_PASS = "password";
    public String ATTR_IS_ADMIN = "isAdmin";
    public String ATTR_EMAIL = "email";
    //registrar <-> user M:N relationship table
    public String TABLE_USER_REGISTRAR_NAME = "User_Registrar";
    public String USER_REGISTRAR_ATTR_REGISTRAR_ID = "registrarId";
    public String USER_REGISTRAR_ATTR_USER_ID = "userAccountId";

    public Long insertUser(User user) throws DatabaseException, AlreadyPresentException;

    public User getUserById(long id, boolean includePassword) throws DatabaseException, RecordNotFoundException;

    public User getUserByLogin(String login, boolean includePassword) throws DatabaseException, RecordNotFoundException;

    public List<Long> getAdminsOfRegistrar(long registrarId) throws DatabaseException, RecordNotFoundException;

    public List<Long> getAllUsersId() throws DatabaseException;

    public List<User> getAllUsers(boolean includePasswords) throws DatabaseException;

    public void updateUser(User user) throws DatabaseException, RecordNotFoundException;

    public void deleteUser(long id) throws DatabaseException, RecordNotFoundException;

    public void deleteAllUsers() throws DatabaseException;
}
