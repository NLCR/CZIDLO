/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.dto.Registrar;
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

    public void checkAccessRights(long registrarId, long userId) throws AccessException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            checkAccessRights(registrar, userId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void checkAccessRights(Sigla registrarSigla, long userId) throws AccessException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarBySigla(registrarSigla);
            checkAccessRights(registrar, userId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void checkAccessRights(Registrar registrar, long userId) throws AccessException {
        try {
            List<Long> adminsOfRegistrar = factory.userDao().getAdminsOfRegistrar(registrar.getId());
            if (!adminsOfRegistrar.contains(userId)) {
                throw new AccessException(userId, registrar.getUrnInstitutionCode());
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
