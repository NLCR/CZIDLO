/*
 * Copyright (C) 2012 Martin Řehánek
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
package cz.nkp.urnnbn.config;

/**
 * Properties shared among all modules. All the keys are prefixed with "czidlo".
 *
 * @author Martin Řehánek
 */
public class PropertyKeys {

    public static final String SERVER_READ_ONLY = "czidlo.readOnly";
    public static final String DEVEL = "czidlo.develMode";
    public static final String LANGUAGE_CODE = "czidlo.languageCode";
    public static final String ADMIN_NAME = "czidlo.admin.name";
    public static final String ADMIN_EMAIL = "czidlo.admin.email";
    public static final String DB_DRIVER = "czidlo.db.driver";
    public static final String DB_HOST = "czidlo.db.host";
    public static final String DB_PORT = "czidlo.db.port";
    public static final String DB_DATABASE = "czidlo.db.database";
    public static final String DB_LOGIN = "czidlo.db.login";
    public static final String DB_PASSWORD = "czidlo.db.password";
    public static final String ADMIN_LOG_FILE = "czidlo.admin.logFile";
}
