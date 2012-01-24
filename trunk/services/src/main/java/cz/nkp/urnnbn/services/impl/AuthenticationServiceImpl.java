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
    public boolean authenticate(User user) {
        try {
            factory.userDao().getUserByLogin(user.getLogin());
        } catch (DatabaseException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RecordNotFoundException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TODO: return real result
        return true;
    }
}
