/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import java.util.Collection;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ContentNotFoundException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataUpdateService extends BusinessService {

    public void updateRegistrarScopeIdentifier(String login, RegistrarScopeIdentifier id) throws UnknownUserException, AccessException,
            UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException;

    public void updateDigitalDocument(DigitalDocument doc, String login) throws UnknownUserException, AccessException, UnknownDigDocException;

    public void updateDigitalInstance(DigitalInstance instance, String login) throws UnknownUserException, AccessException, UnknownDigInstException;

    public void updateRegistrar(Registrar registrar, String login) throws UnknownUserException, AccessException, UnknownRegistrarException;

    public void updateArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException, UnknownArchiverException;

    public void updateDigitalLibrary(DigitalLibrary library, String login) throws UnknownUserException, AccessException, UnknownDigLibException;

    public void updateCatalog(Catalog catalog, String login) throws UnknownUserException, AccessException, UnknownCatalogException;

    public void updateIntelectualEntity(IntelectualEntity entity, Originator originator, Publication publication, SourceDocument srcDoc,
            Collection<IntEntIdentifier> identifiers, String login) throws UnknownUserException, NotAdminException, UnknownIntelectualEntity,
            IdentifierConflictException;

    public void updateUser(User user, String login) throws UnknownUserException, NotAdminException;

    public void updateContent(Content content, String login) throws UnknownUserException, NotAdminException, ContentNotFoundException;

}
