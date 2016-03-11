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
     * Verifies that user has access_right to registar.
     *
     * @param registrarId
     *            internal id of registar
     * @param login
     *            login of user that is supposed to have access right to registrar
     * @throws AccessException
     *             if access right of user to registar doesn't exist
     * @throws UnknownUserException
     *             if no such user with this login exists
     */
    public void checkAccessRights(long registrarId, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            checkAccessRights(registrar, userByLogin(login));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user has access_right to registar or is administrator.
     *
     * @param registrarId
     *            internal id of registar
     * @param login
     *            login of user that is supposed to have access right to registrar
     * @throws AccessException
     *             if access right of user to registar doesn't exist
     * @throws UnknownUserException
     *             if no such user with this login exists
     */
    public void checkAccessRightsOrAdmin(long registrarId, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            if (!isAdmin(login)) {
                checkAccessRights(registrar, userByLogin(login));
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void checkAccessRightsOrAdmin(RegistrarCode registrarCode, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(registrarCode);
            if (!isAdmin(login)) {
                checkAccessRights(registrar, userByLogin(login));
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifies that user has access_right to registar.
     *
     * @param registrarCode
     *            code of registrar
     * @param login
     *            login of user that is supposed to have access right to registrar
     * @throws AccessException
     *             if access right of user to registar doesn't exist
     * @throws UnknownUserException
     *             if no such user with this login exists
     */
    public void checkAccessRights(RegistrarCode registrarCode, String login) throws AccessException, UnknownUserException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(registrarCode);
            checkAccessRights(registrar, userByLogin(login));
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

    private void checkAccessRights(Registrar registrar, User user) throws AccessException {
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

    public void checkAdminRights(String login) throws NotAdminException, UnknownUserException {
        if (!isAdmin(login)) {
            throw new NotAdminException(login);
        }
    }

    private boolean isAdmin(String login) throws UnknownUserException {
        User user = userByLogin(login);
        return (user.isAdmin());
    }
}
