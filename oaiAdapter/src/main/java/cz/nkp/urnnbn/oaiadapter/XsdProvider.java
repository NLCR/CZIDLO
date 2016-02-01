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
package cz.nkp.urnnbn.oaiadapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author Martin Řehánek
 */
public class XsdProvider {

    private final String digDocRegistrationDataXsd;
    private final String digInstImportDataXsd;

    public XsdProvider(URL digDocRegistrationDataXsdUrl, URL digInstImportXsdUrl) throws IOException {
        digDocRegistrationDataXsd = loadXsdFromUrl(digDocRegistrationDataXsdUrl);
        digInstImportDataXsd = loadXsdFromUrl(digInstImportXsdUrl);
    }

    // TODO: use cz.nkp.urnnbn.oaiadapter.utils.getDocument instead (dev certificate, etc)
    private String loadXsdFromUrl(URL xsd) throws IOException {
        URLConnection connection = xsd.openConnection();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public String getDigitalDocumentRegistrationDataXsd() {
        return digDocRegistrationDataXsd;
    }

    public String getDigitalInstanceImportDataXsd() {
        return digInstImportDataXsd;
    }
}
