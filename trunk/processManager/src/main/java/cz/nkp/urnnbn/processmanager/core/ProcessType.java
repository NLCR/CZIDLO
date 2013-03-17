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
package cz.nkp.urnnbn.processmanager.core;

/**
 * Enumerates types of implemented processes. Add new value here if adding new
 * process type. It may seem more appropriate for this class to be present in module jobs.
 * But that would cause cyclic dependency services->jobs->services
 *
 * @author Martin Řehánek
 */
public enum ProcessType {

    /**
     * Exports list of records owned by registrar in csv format. Such record
     * contains only aggregate title and urn:nbn. Agregate title means
     * combination of title, subtitle, volume title and issue title according to
     * what is available for given type of intelectual entity.
     */
    REGISTRARS_URN_NBN_CSV_EXPORT,
    /**
     * Harvests external OAI-PMH repository, transforms records into import xmls
     * and registers DD and imports DI into resolver.
     */
    OAI_ADAPTER,
    /**
     * Test process.
     */
    TEST
}
