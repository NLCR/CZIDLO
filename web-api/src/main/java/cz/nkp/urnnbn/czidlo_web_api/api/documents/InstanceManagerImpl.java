package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.DigInst;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.*;

public class InstanceManagerImpl implements InstanceManager {

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    @Override
    public boolean deactivateInstance(long instanceId, String login) throws UnknownRecordException, InsufficientRightsException {
        try {
            DigitalInstance digitalInstance = dataAccessService().digInstanceByInternalId(instanceId);
            if (digitalInstance == null) {
                throw new UnknownRecordException("Digital instance with id " + instanceId + " does not exist.");
            }
            if (!digitalInstance.isActive()) {
                return false; // already inactive
            }
            dataUpdateService().deactivateDigitalInstance(instanceId, login);
            return true; // successfully deactivated
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("User with login " + login + " has insufficient rights to deactivate digital instance with id " + instanceId + ".");
        } catch (UnknownDigInstException e) {
            throw new UnknownRecordException("Digital instance with id " + instanceId + " does not exist.");
        }
    }

    @Override
    public DigInst getDigitalInstanceById(long instanceId) throws UnknownRecordException {
        DigitalInstance digitalInstance = dataAccessService().digInstanceByInternalId(instanceId);
        if (digitalInstance == null) {
            throw new UnknownRecordException("Digital instance with id " + instanceId + " does not exist.");
        }
        DigitalLibrary library = dataAccessService().libraryByInternalId(digitalInstance.getLibraryId());
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        return DigInst.from(digitalInstance,
                dataAccessService().libraryByInternalId(digitalInstance.getLibraryId()),
                registrar.getCode().toString());
    }

    @Override
    public void updateDigitalInstance(long instanceId, String login, String url, String format, String accessibility, AccessRestriction accessRestriction) throws UnknownRecordException, InsufficientRightsException {
        try {
            DigitalInstance digitalInstance = dataAccessService().digInstanceByInternalId(instanceId);
            if (digitalInstance == null) {
                throw new UnknownRecordException("Digital instance with id " + instanceId + " does not exist.");
            }
            digitalInstance.setUrl(url);
            digitalInstance.setFormat(format);
            digitalInstance.setAccessibility(accessibility);
            digitalInstance.setAccessRestriction(accessRestriction);
            dataUpdateService().updateDigitalInstance(digitalInstance, login);
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("User with login " + login + " has insufficient rights to update digital instance with id " + instanceId + ".");
        } catch (UnknownDigInstException e) {
            throw new UnknownRecordException("Digital instance with id " + instanceId + " does not exist.");
        }
    }

    @Override
    public DigInst createDigitalInstance(String login, UrnNbn urnNbn, long libraryId, String url, String format, String accessibility, AccessRestriction accessRestriction) throws UnknownRecordException, InsufficientRightsException {
        try {
            UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
            if (urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.FREE || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.RESERVED || urnNbnWithStatus.getUrn() == null) {
                throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " does not exist.");
            }
            DigitalInstance instance = new DigitalInstance();
            instance.setLibraryId(libraryId);
            instance.setDigDocId(urnNbnWithStatus.getUrn().getDigDocId());
            instance.setUrl(url);
            instance.setFormat(format);
            instance.setAccessibility(accessibility);
            instance.setAccessRestriction(accessRestriction);
            DigitalInstance created = dataImportService().addDigitalInstance(instance, login);
            return DigInst.from(created,
                    dataAccessService().libraryByInternalId(created.getLibraryId()),
                    dataAccessService().registrarById(dataAccessService().libraryByInternalId(created.getLibraryId()).getRegistrarId()).getCode().toString());
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("User with login " + login + " has insufficient rights to create new digital instance in library with id " + libraryId + ".");
        } catch (UnknownDigLibException | UnknownDigDocException e) {
            throw new UnknownRecordException(e.getMessage());
        }
    }
}
