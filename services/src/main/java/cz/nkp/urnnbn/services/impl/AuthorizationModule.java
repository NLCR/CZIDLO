/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class AuthorizationModule {

    private static final Logger logger = Logger.getLogger(AuthorizationModule.class.getName());
    private final DAOFactory factory;

    public AuthorizationModule(DAOFactory factory) {
        this.factory = factory;
    }

    public void checkAccessRights(long registrarId, String login) throws AccessException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            checkAccessRights(registrar, userByLogin(login));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void checkAccessRights(RegistrarCode registrarCode, String login) throws AccessException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(registrarCode);
            checkAccessRights(registrar, userByLogin(login));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private User userByLogin(String login) {
        try {
            return factory.userDao().getUserByLogin(login);
        } catch (DatabaseException ex) {
            Logger.getLogger(AuthorizationModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (RecordNotFoundException ex) {
            Logger.getLogger(AuthorizationModule.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void checkAccessRights(Registrar registrar, User user) throws AccessException {
        try {
            List<Long> adminsOfRegistrar = factory.userDao().getAdminsOfRegistrar(registrar.getId());
            if (!adminsOfRegistrar.contains(user.getId())) {
                throw new AccessException(user.getId(), registrar.getCode());
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
