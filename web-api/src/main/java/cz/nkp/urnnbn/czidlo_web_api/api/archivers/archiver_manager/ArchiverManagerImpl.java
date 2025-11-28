package cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

import java.util.ArrayList;
import java.util.List;

public class ArchiverManagerImpl implements ArchiverManager {

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
    public Archiver createArchiver(String login, String name, String description) {
        cz.nkp.urnnbn.core.dto.Archiver dtoArchiver = new cz.nkp.urnnbn.core.dto.Archiver();
        dtoArchiver.setName(name);
        dtoArchiver.setDescription(description);
        try {
            dtoArchiver = dataImportService().insertNewArchiver(dtoArchiver, login);
            return Archiver.fromDto(dtoArchiver);
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Archiver getArchiver(long archiverId) throws UnknownRecordException {
        cz.nkp.urnnbn.core.dto.Archiver dtoArchiver = dataAccessService().archiverById(archiverId);
        if (dtoArchiver == null) {
            throw new UnknownRecordException("Unknown archiver: " + archiverId);
        }
        return Archiver.fromDto(dtoArchiver);
    }

    @Override
    public List<Archiver> getArchivers() {
        List<cz.nkp.urnnbn.core.dto.Archiver> dtoArchivers = dataAccessService().archivers();
        List<Archiver> archivers = new ArrayList<>(dtoArchivers.size());
        for (cz.nkp.urnnbn.core.dto.Archiver dtoArchiver : dtoArchivers) {
            archivers.add(Archiver.fromDto(dtoArchiver));
        }
        return archivers;
    }

    @Override
    public Archiver updateArchiver(String login, long archiverId, String name, String description, boolean hidden) throws UnknownRecordException {
        cz.nkp.urnnbn.core.dto.Archiver dtoArchiver = new cz.nkp.urnnbn.core.dto.Archiver();
        dtoArchiver.setId(archiverId);
        dtoArchiver.setName(name);
        dtoArchiver.setDescription(description);
        dtoArchiver.setHidden(hidden);
        try {
            dataUpdateService().updateArchiver(dtoArchiver, login);
            dtoArchiver = dataAccessService().archiverById(archiverId);
            return Archiver.fromDto(dtoArchiver);
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        } catch (UnknownArchiverException e) {
            throw new UnknownRecordException("Unknown archiver: " + archiverId);
        }
    }

    @Override
    public void deleteArchiver(String login, long archiverId) throws UnknownRecordException {
        try {
            cz.nkp.urnnbn.core.dto.Archiver dtoArchiver = dataAccessService().archiverById(archiverId);
            if (dtoArchiver == null) {
                throw new UnknownRecordException("Unknown archiver: " + archiverId);
            }
            dataRemoveService().removeArchiver(archiverId, login);
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        } catch (UnknownArchiverException e) {
            throw new UnknownRecordException("Unknown archiver: " + archiverId);
        } catch (CannotBeRemovedException e) {
            throw new RuntimeException(e);
        }
    }
}
