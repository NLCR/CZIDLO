/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.AuthenticationService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class AuthenticationServiceImpl extends BusinessServiceImpl implements AuthenticationService {

    public AuthenticationServiceImpl(DatabaseConnector con) {
        super(con);
    }

    @Override
    public User autheticatedUserOrNull(User user) {
        try {
            if (user == null || user.getLogin() == null || user.getPassword() == null) {
                return null;
            }
            User userByLogin = factory.userDao().getUserByLogin(user.getLogin(), true);
            if (userByLogin.getPassword().equals(user.getPassword())) {
                return userByLogin;
            } else {
                return null;
            }
        } catch (DatabaseException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (RecordNotFoundException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
