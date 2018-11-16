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
package cz.nkp.urnnbn.processmanager.conf;

/**
 *
 * @author Martin Řehánek
 */
public class CzidloDatabaseCredentials {

    private final String host;
    private final Integer port;
    private final String database;
    private final String login;
    private final String password;

    public CzidloDatabaseCredentials(String host, Integer port, String database, String login, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.login = login;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
