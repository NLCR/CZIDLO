package cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

import java.util.List;

public interface ArchiverManager {

    /**
     * Creates an archiver.
     *
     * @param login         login of user creating the archiver
     * @param name          unique name of the archiver
     * @param description   description of the archiver
     * @return instance of the archiver
     * @throws DuplicateRecordException if an archiver of that name already exists
     */
    public Archiver createArchiver(String login, String name, String description) throws DuplicateRecordException;

    /**
     * Returns an archiver with ID.
     *
     * @param archiverId    id of the archiver
     * @return instance of the archiver
     * @throws UnknownRecordException if an archiver with that ID does not exist
     */
    public Archiver getArchiver(long archiverId) throws UnknownRecordException;

    /**
     * Returns all archivers.
     *
     * @return list of all archivers
     */
    public List<Archiver> getArchivers();

    /**
     * Updates an archiver.
     *
     * @param login         login of user updating the archiver
     * @param archiverId    id of the archiver
     * @param name          unique name of the archiver
     * @param description   description of the archiver
     * @param hidden        if the archiver is hidden
     * @return instance of the archiver
     * @throws UnknownRecordException if an archiver with that ID does not exist
     * @throws DuplicateRecordException if an archiver of that name already exists
     */
    public Archiver updateArchiver(String login, long archiverId, String name, String description, boolean hidden) throws UnknownRecordException, DuplicateRecordException;

    /**
     * Deletes an archiver.
     *
     * @param login         login of user creating the archiver
     * @param archiverId    id of the archiver
     * @throws UnknownRecordException if an archiver with that ID does not exist
     */
    public void deleteArchiver(String login, long archiverId) throws UnknownRecordException;
}
