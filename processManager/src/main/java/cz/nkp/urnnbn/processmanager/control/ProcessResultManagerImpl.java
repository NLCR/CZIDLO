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

import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.persistence.AuthorizingProcessDAOImpl;
import cz.nkp.urnnbn.processmanager.persistence.AuthrozingProcessDAO;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcessFileUtils;
import java.io.File;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessResultManagerImpl implements ProcessResultManager {

    private static ProcessResultManager instance;
    private final AuthrozingProcessDAO processDao = AuthorizingProcessDAOImpl.instanceOf();

    public static ProcessResultManager instanceOf() {
        if (instance == null) {
            instance = new ProcessResultManagerImpl();
        }
        return instance;
    }

    @Override
    public File getProcessLogFile(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        cz.nkp.urnnbn.processmanager.core.Process process = processDao.getProcess(login, processId);
        if (process.getState() == ProcessState.SCHEDULED || process.getState() == ProcessState.CANCELED) {
            throw new InvalidStateException(processId, process.getState());
        }
        return ProcessFileUtils.buildLogFile(processId);
    }

    @Override
    public File getProcessLogFile(Long processId) throws UnknownRecordException, InvalidStateException {
        cz.nkp.urnnbn.processmanager.core.Process process = processDao.getProcess(processId);
        if (process.getState() == ProcessState.SCHEDULED || process.getState() == ProcessState.CANCELED) {
            throw new InvalidStateException(processId, process.getState());
        }
        return ProcessFileUtils.buildLogFile(processId);
    }

    @Override
    public File getProcessOutputFile(String login, Long processId, String filename) throws UnknownRecordException, AccessRightException, InvalidStateException {
        cz.nkp.urnnbn.processmanager.core.Process process = processDao.getProcess(login, processId);
        if (process.getState() != ProcessState.FINISHED) {
            throw new InvalidStateException(processId, process.getState());
        }
        return ProcessFileUtils.buildProcessFile(processId, filename);
    }

    @Override
    public File getProcessOutputFile(Long processId, String filename) throws UnknownRecordException, InvalidStateException {
        cz.nkp.urnnbn.processmanager.core.Process process = processDao.getProcess(processId);
        if (process.getState() != ProcessState.FINISHED) {
            throw new InvalidStateException(processId, process.getState());
        }
        return ProcessFileUtils.buildProcessFile(processId, filename);
    }
}
