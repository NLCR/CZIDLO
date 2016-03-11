/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v2_v3;

import java.util.logging.Level;

import cz.nkp.urnnbn.api.v3.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.RegistrarScopeIdentifiersBuilder;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractDigitalInstanceResource extends ApiV2V3Resource {

    private final DigitalInstance instance;

    public AbstractDigitalInstanceResource(DigitalInstance instance) {
        this.instance = instance;
    }

    public abstract String getDigitalInstanceXmlRecord();

    public final String getDigitalInstanceApiV3XmlRecord() {
        return digitalInstanceXmlBuilder(instance, true, true).buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstanceBuilder digitalInstanceXmlBuilder(DigitalInstance instance, boolean withDigDoc, boolean withDigLib) {
        DigitalDocumentBuilder digDocBuilder = withDigDoc ? digDocBuilder(instance.getDigDocId()) : null;
        DigitalLibraryBuilder libBuilder = withDigLib ? digLibBuilder(instance.getLibraryId()) : null;
        return new DigitalInstanceBuilder(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilder digDocBuilder(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilder(digDocId);
        return new DigitalDocumentBuilder(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    protected final String deactivateDigitalInstance(String login) {
        DigitalInstance found = dataAccessService().digInstanceByInternalId(instance.getId());
        if (!found.isActive()) {
            throw new DigitalInstanceAlreadyDeactivatedException(instance);
        } else {
            deactivateDigitalInstanceWithServiceExceptionTranslation(login);
            DigitalInstance deactivated = dataAccessService().digInstanceByInternalId(instance.getId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(deactivated, deactivated.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
        }
    }

    private void deactivateDigitalInstanceWithServiceExceptionTranslation(String login) {
        try {
            dataRemoveService().deactivateDigitalInstance(instance.getId(), login);
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigInstException ex) {
            // should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }
}
