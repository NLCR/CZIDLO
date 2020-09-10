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
package cz.nkp.urnnbn.api.v3.v3_abstract;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nu.xom.Document;
import cz.nkp.urnnbn.api.v3.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.api.v3.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.v3.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v3.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv3.unmarshallers.DigitalInstanceUnmarshaller;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractDigitalInstancesResource extends ApiResource {

    protected final DigitalDocument digDoc;

    public AbstractDigitalInstancesResource(DigitalDocument digDoc) {
        this.digDoc = digDoc;
    }

    public abstract String getDigitalInstancesXmlRecord();

    protected final String getDigitalInstancesApiV3XmlRecord() {
        DigitalInstancesBuilder builder = instancesBuilder();
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstancesBuilder instancesBuilder() {
        if (digDoc == null) {
            return new DigitalInstancesBuilder(dataAccessService().digitalInstancesCount());
        } else {
            List<DigitalInstanceBuilder> instanceBuilders = instanceBuilders(digDoc);
            return new DigitalInstancesBuilder(instanceBuilders);
        }
    }

    private List<DigitalInstanceBuilder> instanceBuilders(DigitalDocument doc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(doc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, instance.getLibraryId());
            result.add(builder);
        }
        return result;
    }

    protected final DigitalInstance digitalInstanceFromApiV3Document(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    protected final String addNewDigitalInstanceWithApiV3Response(DigitalInstance digitalInstance, String login) {
        try {
            Parser.parseUrl(digitalInstance.getUrl());
            checkNoOtherDigInstInSameLibraryPresent(digitalInstance);
            DigitalInstance digInstInserted = dataImportService().addDigitalInstance(digitalInstance, login);
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(digInstInserted, digitalInstance.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
            // String responseXml = builder.buildDocumentWithResponseHeader().toXML();
            // return Response.created(null).entity(responseXml).build();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigLibException ex) {
            throw new UnknownDigitalLibraryException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
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

    public abstract AbstractDigitalInstanceResource getDigitalInstanceResource(String digitalInstanceIdStr);

    protected final DigitalInstance getDigitalInstance(long digitalInstanceId) {
        DigitalInstance instance = dataAccessService().digInstanceByInternalId(digitalInstanceId);
        if (instance == null) {
            throw new UnknownDigitalInstanceException(digitalInstanceId);
        } else {
            return instance;
        }
    }
}
