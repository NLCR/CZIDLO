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

/**
 *
 * @author Martin Řehánek
 */
public class UnknownRecordException extends Exception {

    /**
     * Creates a new instance of
     * <code>UnknownRecordException</code> without detail message.
     */
    public UnknownRecordException() {
    }

    /**
     * Constructs an instance of
     * <code>UnknownRecordException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownRecordException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of
     * <code>UnknownRecordException</code> without cause exception.
     *
     * @param cause cause
     */
    public UnknownRecordException(Throwable cause) {
        super(cause);
    }
}
