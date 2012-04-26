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

    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getName());

    public AuthenticationServiceImpl(DatabaseConnector con) {
        super(con);
    }

    @Override
    public User autheticatedUserOrNull(String login, String password) {
        try {
            if (login == null || password == null) {
                return null;
            }
            User userByLogin = factory.userDao().getUserByLogin(login, true);
            if (userByLogin.getPassword().equals(password)) {
                return userByLogin;
            } else {
                return null;
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
