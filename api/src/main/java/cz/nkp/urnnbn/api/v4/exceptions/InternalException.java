/*
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
package cz.nkp.urnnbn.api.v4.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response.Status;

public class InternalException extends ApiV4Exception {

    public InternalException(String errorMessage) {
        super(Status.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", errorMessage);
    }

    public InternalException(Throwable e) {
        super(Status.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", buildMessage(e));
        e.printStackTrace();
    }

    private static String buildMessage(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        if (e.getMessage() != null) {
            out.write(e.getMessage());
        }
        e.printStackTrace(out);
        out.flush();
        return writer.toString();
    }
}
