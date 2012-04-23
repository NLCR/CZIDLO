/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.Collection;

/**
 *
 * @author Martin Řehánek
 */
public interface DataUpdateService extends BusinessService {

    //TODO: access rights
    public void updateDigRepIdentifier(DigDocIdentifier id) throws
            UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException;

    public void updateDigitalDocument(DigitalDocument doc, String login) throws
            UnknownUserException, AccessException,
            UnknownDigDocException;

    public void updateRegistrar(Registrar registrar, String login) throws
            UnknownUserException, AccessException,
            UnknownRegistrarException;

    public void updateArchiver(Archiver archiver, String login) throws
            UnknownUserException, NotAdminException,
            UnknownArchiverException;

    public void updateDigitalLibrary(DigitalLibrary library, String login) throws
            UnknownUserException, AccessException,
            UnknownDigLibException;

    public void updateCatalog(Catalog catalog, String login) throws
            UnknownUserException, AccessException,
            UnknownCatalogException;

    public void updateIntelectualEntity(IntelectualEntity entity, Originator originator, Publication publication, SourceDocument srcDoc, Collection<IntEntIdentifier> identifiers, String login) throws
            UnknownUserException, NotAdminException,
            UnknownIntelectualEntity, IdentifierConflictException;
}
