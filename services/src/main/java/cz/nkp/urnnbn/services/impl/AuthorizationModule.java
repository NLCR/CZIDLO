/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.InvalidUserException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

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

    /**
     * Verifies that user has access_right to registrar.
     *
     * @param registrarId internal id of registrar
     * @param login       login of user that is supposed to have access right to registrar
     * @throws AccessException      if access right of user to registrar doesn't exist
     * @throws UnknownUserException if no such user with this login exists
     */
    public void checkRegistrarRights(long registrarId, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            checkRegistrarRights(registrar, userByLogin(login));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user has access_right to registrar or is administrator.
     *
     * @param registrarId internal id of registrar
     * @param login       login of user that is supposed to have access right to registrar
     * @throws AccessException      if access right of user to registrar doesn't exist
     * @throws UnknownUserException if no such user with this login exists
     */
    public void checkRegistrarRightsOrAdmin(long registrarId, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            if (!isAdmin(login)) {
                checkRegistrarRights(registrar, userByLogin(login));
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user has access_right to registrar or is administrator.
     *
     * @param registrarCode code of registrar
     * @param login         login of user that is supposed to have access right to registrar
     * @throws AccessException      if access right of user to registrar doesn't exist
     * @throws UnknownUserException if no such user with this login exists
     */
    public void checkRegistrarRightsOrAdmin(RegistrarCode registrarCode, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(registrarCode);
            if (!isAdmin(login)) {
                checkRegistrarRights(registrar, userByLogin(login));
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user has access_right to registrar.
     *
     * @param registrarCode code of registrar
     * @param login         login of user that is supposed to have access right to registrar
     * @throws AccessException      if access right of user to registrar doesn't exist
     * @throws UnknownUserException if no such user with this login exists
     */
    public void checkRegistrarRights(RegistrarCode registrarCode, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(registrarCode);
            checkRegistrarRights(registrar, userByLogin(login));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private User userByLogin(String login) throws UnknownUserException {
        try {
            return factory.userDao().getUserByLogin(login);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new UnknownUserException(login);
        }
    }

    private void checkRegistrarRights(Registrar registrar, User user) throws AccessException {
        try {
            List<Long> adminsOfRegistrar = factory.userDao().getAdminsOfRegistrar(registrar.getId());
            if (!adminsOfRegistrar.contains(user.getId())) {
                throw new AccessException(user.getLogin(), registrar.getCode());
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user is administrator.
     *
     * @param login
     * @throws NotAdminException
     * @throws UnknownUserException
     */
    public void checkAdmin(String login) throws NotAdminException, UnknownUserException {
        if (!isAdmin(login)) {
            throw new NotAdminException(login);
        }
    }

    private boolean isAdmin(String login) throws UnknownUserException {
        User user = userByLogin(login);
        return (user.isAdmin());
    }

    /**
     * Verifies that user is either administrator or the same user as specified.
     *
     * @param targetUser user that is supposed to "managable" by the user with given login
     * @param login      login of user that is supposed to be admin or the same user
     * @throws UnknownUserException
     * @throws AccessException      if user is neither admin nor the same user
     */
    public void checkSameUserOrAdmin(User targetUser, String login) throws UnknownUserException, AccessException {
        User performingUser = userByLogin(login);
        if (performingUser.isAdmin()) {
            return;
        }
        if (!performingUser.getId().equals(targetUser.getId())) {
            throw new AccessException(login, targetUser);
        }
    }
}
