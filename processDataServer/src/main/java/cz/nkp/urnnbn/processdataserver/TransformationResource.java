/*
 * Copyright (C) 2017 Martin Řehánek
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

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class TransformationResource {

    private final XmlTransformation transformation;

    public TransformationResource(XmlTransformation transformation) {
        this.transformation = transformation;
    }

    @GET
    @Produces("application/xml")
    public String getXml() {
        return new TransformationXmlBuilder(transformation).buildXml();
    }

    @GET
    @Path("xslt")
    @Produces("text/xml")
    public Response getFile() {
        ResponseBuilder builder = Response.ok().entity(transformation.getXslt());
        return builder.build();
    }

    @GET
    @Path("xsltFile")
    public Response getProcessOutput() {
        String filename = transformation.getOwnerLogin() + transformation.getId() + ".xslt";
        String mimeType = "text/xml; charset=UTF-8";
        ResponseBuilder builder = Response.ok(transformation.getXslt(), mimeType);
        builder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        return builder.build();
    }

}
