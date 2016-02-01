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

import cz.nkp.urnnbn.processmanager.core.Process;

/**
 *
 * @author Martin Řehánek
 */
public class AccessRightException extends Exception {

    public AccessRightException(String login, Process process) {
        super("user (login=" + login + ")" + " is not admin nor owner of process " + process.toString());
    }

    public AccessRightException(String login, Long processId) {
        super("user (login=" + login + ")" + " is not admin nor owner of process " + processId);
    }

    /**
     * Creates a new instance of <code>AccessRightException</code> without detail message.
     */
    public AccessRightException() {
    }

    /**
     * Constructs an instance of <code>AccessRightException</code> with the specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public AccessRightException(String msg) {
        super(msg);
    }
}
