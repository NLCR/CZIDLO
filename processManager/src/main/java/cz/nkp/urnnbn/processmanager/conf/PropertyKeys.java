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
package cz.nkp.urnnbn.processmanager.conf;

/**
 *
 * @author Martin Řehánek
 */
public class PropertyKeys extends cz.nkp.urnnbn.config.PropertyKeys {

    public static final String JOBS_DATA_DIR = "process.scheduler.jobsDataDir";
    public static final String MAX_ADMIN_JOBS = "process.scheduler.maxRunning.admin";
    public static final String MAX_USER_JOBS = "process.scheduler.maxRunning.user";
}
