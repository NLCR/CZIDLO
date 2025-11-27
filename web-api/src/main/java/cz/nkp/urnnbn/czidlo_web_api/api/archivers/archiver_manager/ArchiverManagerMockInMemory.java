package cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ArchiverManagerMockInMemory implements ArchiverManager {
    private static final ArchiverInMemoryRepo repo = ArchiverInMemoryRepo.getInstance();

    public ArchiverManagerMockInMemory() {
        Archiver a0 = repo.create("Digitalní repozitář Jihomoravského kraje", "Datové úložiště Jihomoravského kraje");
        a0.setCreated(Date.from(Instant.parse("2025-06-07T10:11:12.028231961Z")));
        a0.setModified(Date.from(Instant.parse("2025-06-07T11:12:13.028231961Z")));

        Archiver a1 = repo.create("Národní filmový archiv", null);
        a1.setCreated(Date.from(Instant.parse("2025-07-08T12:13:14.028231961Z")));
        a1.setModified(Date.from(Instant.parse("2025-07-08T13:14:15.028231961Z")));

        Archiver a2 = repo.create("Úložiště Technologického centra Kraje Vysočina", null);
        a2.setCreated(Date.from(Instant.parse("2025-08-09T14:15:16.028231961Z")));
        a2.setModified(Date.from(Instant.parse("2025-08-09T14:16:17.028231961Z")));
        a2.setHidden(true);

        Archiver a3 = repo.create("Zlínský kraj - úložiště", "Datové úložiště Zlínského kraje");
        a3.setCreated(Date.from(Instant.parse("2025-08-10T14:15:16.028231961Z")));
        a3.setModified(Date.from(Instant.parse("2025-08-10T14:16:17.028231961Z")));
        a3.setHidden(true);
    }

    @Override
    public Archiver createArchiver(String login, String name, String description) throws DuplicateRecordException {
        return repo.create(name, description);
    }

    @Override
    public Archiver getArchiver(long archiverId) throws UnknownRecordException {
        Archiver archiver = repo.getById(archiverId);

        if (archiver == null) {
            throw new UnknownRecordException("Unknown archiver: " + archiverId);
        }
        return archiver;
    }

    @Override
    public List<Archiver> getArchivers() {
        return repo.getAll();
    }

    @Override
    public Archiver updateArchiver(String login, long archiverId, String name, String description, boolean hidden) throws UnknownRecordException, DuplicateRecordException {
        return repo.update(archiverId, name, description, hidden);
    }

    @Override
    public void deleteArchiver(String login, long archiverId) throws UnknownRecordException {
        getArchiver(archiverId);
        repo.delete(archiverId);
    }
}
