/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.AuthenticationService;
import cz.nkp.urnnbn.utils.CryptoUtils;

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
            User userByLogin = factory.userDao().getUserByLogin(login);
            if (passwordMatches(userByLogin, password)) {
                return userByLogin;
            } else {
                return null;
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, "No user with login '" + login + "' found", ex);
            return null;
        }
    }

    private boolean passwordMatches(User userByLogin, String password) {
        String hash = CryptoUtils.createSha256Hash(password, userByLogin.getPasswordSalt());
        return userByLogin.getPasswordHash().equals(hash);
    }
}
