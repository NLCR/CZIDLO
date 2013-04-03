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
package cz.nkp.urnnbn.processmanager.control;

import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import java.io.File;

/**
 *
 * @author Martin Řehánek
 */
public interface ProcessResultManager {

    /**
     * Returns process log file.
     *
     * @param login
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is in SCHEDULED or CANCELED
     * state
     */
    @Deprecated
    public File getProcessLogFile(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Returns process log file.
     *
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws InvalidStateException If process is in SCHEDULED or CANCELED
     * state
     */
    public File getProcessLogFile(Long processId) throws UnknownRecordException, InvalidStateException;

    /**
     * Returns output process output file.
     *
     * @param login
     * @param processId
     * @param filename
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is not in FINISHED state
     */
    public File getProcessOutputFile(String login, Long processId, String filename) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Returns output process output file.
     *
     * @param processId
     * @param filename
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws InvalidStateException If process is not in FINISHED state
     */
    public File getProcessOutputFile(Long processId, String filename) throws UnknownRecordException, InvalidStateException;
}
