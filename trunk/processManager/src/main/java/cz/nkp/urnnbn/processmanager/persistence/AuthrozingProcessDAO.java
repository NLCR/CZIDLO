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
package cz.nkp.urnnbn.processmanager.persistence;

import cz.nkp.urnnbn.processmanager.control.AccessRightException;
import cz.nkp.urnnbn.processmanager.core.Process;

/**
 *
 * @author Martin Řehánek
 */
public interface AuthrozingProcessDAO extends ProcessDAO {

    /**
     * Returns process.
     *
     * @param login
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     */
    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException;
}
