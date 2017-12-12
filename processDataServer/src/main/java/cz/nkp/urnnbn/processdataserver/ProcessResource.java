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
package cz.nkp.urnnbn.processdataserver;

import cz.nkp.urnnbn.processmanager.control.InvalidStateException;
import cz.nkp.urnnbn.processmanager.control.ProcessResultManager;
import cz.nkp.urnnbn.processmanager.control.ProcessResultManagerImpl;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.DiUrlAvailabilityCheckJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.IndexationJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.OaiAdapterJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.UrnNbnCsvExportJob;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class ProcessResource {

    private final Process process;

    public ProcessResource(Process process) {
        this.process = process;
    }

    @GET
    @Produces("application/xml")
    public String getXml() {
        return new ProcessXmlBuilder(process).buildXml();
    }

    @GET
    @Path("log")
    @Produces("text/plain")
    public Response getProcessLog() {
        try {
            File file = getProcessResultManager().getProcessLogFile(process.getId());
            ResponseBuilder builder = Response.ok().entity(new FileInputStream(file));
            return builder.build();
        } catch (FileNotFoundException ex) {
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            throw new WebApplicationException(response);
        } catch (UnknownRecordException ex) {
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            throw new WebApplicationException(response);
        } catch (InvalidStateException ex) {
            Response response = Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(response);
        }
    }

    @GET
    @Path("output")
    public Response getProcessOutput() {
        try {
            ProcessOutputFileInfo outputFileInfo = new ProcessOutputFileInfo(process.getType());
            // System.err.println("ouptput file: " + outputFileInfo.getFilename() + ", mimeType: \"" + outputFileInfo.getMimetype() + "\"");
            File file = getProcessResultManager().getProcessOutputFile(process.getId(), outputFileInfo.getFilename());
            ResponseBuilder builder = Response.ok(file, outputFileInfo.getMimetype());
            builder.header("Content-Disposition", "attachment; filename=\"" + outputFileInfo.getFilename() + "\"");
            return builder.build();
        } catch (UnknownRecordException ex) {
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            throw new WebApplicationException(response);
        } catch (InvalidStateException ex) {
            Response response = Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(response);
        }
    }

    private static class ProcessOutputFileInfo {

        private final ProcessType type;

        private ProcessOutputFileInfo(ProcessType type) {
            this.type = type;
        }

        private String getMimetype() {
            switch (type) {
                case REGISTRARS_URN_NBN_CSV_EXPORT:
                case DI_URL_AVAILABILITY_CHECK:
                    return "text/csv; charset=UTF-8";
                case OAI_ADAPTER:
                case INDEXATION:
                    return "text/plain; charset=UTF-8";
                default:
                    throw new RuntimeException("MIME type of process ouptput for process type " + type + " not defined");
            }
        }

        private String getFilename() {
            switch (type) {
                case REGISTRARS_URN_NBN_CSV_EXPORT:
                    return UrnNbnCsvExportJob.CSV_EXPORT_FILE_NAME;
                case OAI_ADAPTER:
                    return OaiAdapterJob.PARAM_REPORT_FILE;
                case DI_URL_AVAILABILITY_CHECK:
                    return DiUrlAvailabilityCheckJob.CSV_EXPORT_FILE_NAME;
                case INDEXATION:
                    return IndexationJob.PARAM_REPORT_FILE;
                default:
                    throw new RuntimeException("Filename of process ouptput for process type " + type + " not defined");
            }
        }
    }

    private ProcessResultManager getProcessResultManager() {
        return ProcessResultManagerImpl.instanceOf();
    }
}
