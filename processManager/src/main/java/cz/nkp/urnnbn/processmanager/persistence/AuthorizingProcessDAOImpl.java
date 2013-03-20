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
import cz.nkp.urnnbn.processmanager.control.AuthentizationUtils;
import cz.nkp.urnnbn.processmanager.core.Process;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class AuthorizingProcessDAOImpl extends ProcessDAOImpl implements AuthrozingProcessDAO {

    private static final Logger logger = Logger.getLogger(AuthorizingProcessDAOImpl.class.getName());
    private static AuthorizingProcessDAOImpl instance = null;

    public static AuthrozingProcessDAO instanceOf() {
        if (instance == null) {
            logger.log(Level.INFO, "instantiating {0}", ProcessDAOImpl.class.getName());
            instance = new AuthorizingProcessDAOImpl();
        }
        return instance;
    }

    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException {
        Process process = getProcess(processId);
        if (!AuthentizationUtils.isAdminOrOwner(login, process)) {
            throw new AccessRightException(login, process);
        }
        return process;
    }
}
