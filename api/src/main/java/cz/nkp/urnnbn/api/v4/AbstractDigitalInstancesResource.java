package cz.nkp.urnnbn.api.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nu.xom.Document;
import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyPresentException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalInstanceException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalLibraryException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.DigitalInstanceUnmarshaller;

public abstract class AbstractDigitalInstancesResource extends V4Resource {
    protected final DigitalDocument digDoc;

    public AbstractDigitalInstancesResource(DigitalDocument digDoc) {
        this.digDoc = digDoc;
    }

    public abstract String getDigitalInstancesXmlRecord();

    protected final String getDigitalInstancesApiV4XmlRecord() {
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

    protected final DigitalInstance digitalInstanceFromApiV4Document(Document xmlDocument) {
        DigitalInstanceUnmarshaller unmarshaller = new DigitalInstanceUnmarshaller(xmlDocument);
        DigitalInstance result = unmarshaller.getDigitalInstance();
        result.setDigDocId(digDoc.getId());
        return result;
    }

    protected final String addNewDigitalInstanceWithApiV4Response(DigitalInstance digitalInstance, String login) {
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
