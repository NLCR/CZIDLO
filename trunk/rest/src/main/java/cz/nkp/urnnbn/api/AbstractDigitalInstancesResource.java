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
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.api.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.unmarshallers.DigitalInstanceUnmarshaller;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import nu.xom.Document;

/**
 *
 * @author Martin Řehánek
 */
public class AbstractDigitalInstancesResource extends Resource {

    protected final DigitalDocument digDoc;

    public AbstractDigitalInstancesResource(DigitalDocument digDoc) {
        this.digDoc = digDoc;
    }

    public String getDigitalInstances() {
        try {
            DigitalInstancesBuilder builder = instancesBuilder();
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigitalInstancesBuilder instancesBuilder() throws DatabaseException {
        if (digDoc == null) {
            return new DigitalInstancesBuilder(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilder> instanceBuilders = instanceBuilders(digDoc);
            return new DigitalInstancesBuilder(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilder> instanceBuilders(DigitalDocument doc) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    protected DigitalInstance digitalInstanceFromApiV3Document(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    public Response addNewDigitalInstance(DigitalInstance digitalInstance, String login) {
        try {
            Parser.parseUrl(digitalInstance.getUrl());
            checkNoOtherDigInstInSameLibraryPresent(digitalInstance);
            DigitalInstance digInstInserted = dataImportService().addDigitalInstance(digitalInstance, login);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(digInstInserted, digitalInstance.getLibraryId());
            String responseXml = builder.buildDocumentWithResponseHeader().toXML();
            return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigLibException ex) {
            throw new UnknownDigitalLibraryException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        }
    }

    private void checkNoOtherDigInstInSameLibraryPresent(DigitalInstance digInstFromClient) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digInstFromClient.getDigDocId());
        for (DigitalInstance instance : instances) {
            if (instance.isActive() && instance.getLibraryId().equals(digInstFromClient.getLibraryId())) {
                throw new DigitalInstanceAlreadyPresentException(instance);
            }
        }
    }

    protected DigitalInstanceResource getDetdigitalInstanceResource(long digitalInstanceId) {
        try {
            DigitalInstance instance = dataAccessService().digInstanceByInternalId(digitalInstanceId);
            if (instance == null) {
                throw new UnknownDigitalInstanceException(digitalInstanceId);
            }
            return new DigitalInstanceResource(instance);
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }
}
