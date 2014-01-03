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

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 *
 * @author Martin Řehánek
 */
public class AuthentizationUtils {

    public static boolean isAdmin(String userLogin) {
        try {
            User user = Services.instanceOf().dataAccessService().userByLogin(userLogin);
            return user.isAdmin();
        } catch (UnknownUserException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean isAdminOrOwner(String userLogin, cz.nkp.urnnbn.processmanager.core.Process process) {
        return isAdmin(userLogin) || process.getOwnerLogin().equals(userLogin);
    }
}
