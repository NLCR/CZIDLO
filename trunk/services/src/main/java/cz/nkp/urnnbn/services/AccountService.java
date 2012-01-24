/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;

/**
 *
 * @author Martin Řehánek
 */
public interface AccountService extends BusinessService{

    /**
     * Creates new registrar account.
     * @param registrar
     * @param registrarAdmin User that will register digitalized documents 
     * for this registrar (instititution)
     * @param applicationAdminId Id of user to perform this operation.
     * Only admin is allowed to create new archivers.
     */
    public void createRegistrar(
            Registrar registrar,
            User registrarAdmin,
            long applicationAdminId);

    /**
     * Creates new archiver that will be available to any institution.
     * @param archiver
     * @param userId Id of User to perform this operation.
     * Only admin is allowed to create new archivers.
     */
    public void createArchiver(
            Archiver archiver,
            long userId);

    /**
     * Creates new catalogue for registrar.
     * @param catalogue
     * @param registrarId 
     * @param user Id of user to perform this operation.
     * Only adminstrator of this organization or application admininstrator
     * are allowed to do that.
     */
    public void createCatalogue(
            Catalog catalogue,
            long registrarId,
            long userId);

    /**
     * Creates new digital library for registrar.
     * @param library
     * @param registrarId Id of user to perform this operation.
     * Only adminstrator of this organization or application admininstrator
     * are allowed to do that.
     */
    public void createDigitalLibrary(
            DigitalLibrary library,
            long registrarId);
//TODO: read operations
}
