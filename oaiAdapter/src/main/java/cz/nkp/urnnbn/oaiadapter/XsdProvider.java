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

import cz.nkp.urnnbn.oaiadapter.utils.ApiResponse;
import cz.nkp.urnnbn.oaiadapter.utils.HttpConnector;

import java.io.IOException;
import java.net.URL;

/**
 * @author Martin Řehánek
 */
public class XsdProvider {

    private final String ddRegistrationDataXsd;
    private final String diImportDataXsd;
    private final URL ddRegistrationDataXsdUrl;
    private final URL digInstImportXsdUrl;
    private final HttpConnector httpConnector = new HttpConnector();

    public XsdProvider(URL ddRegistrationDataXsdUrl, URL digInstImportXsdUrl) throws IOException {
        this.ddRegistrationDataXsdUrl = ddRegistrationDataXsdUrl;
        this.digInstImportXsdUrl = digInstImportXsdUrl;
        ddRegistrationDataXsd = loadXsdFromUrl(ddRegistrationDataXsdUrl);
        diImportDataXsd = loadXsdFromUrl(digInstImportXsdUrl);
    }

    private String loadXsdFromUrl(URL xsd) throws IOException {
        ApiResponse apiResponse = httpConnector.httpGet(xsd, null, false);
        if (apiResponse.getHttpCode() == 200) {
            return apiResponse.getBody();
        } else {
            throw new IOException("HTTP " + apiResponse.getHttpCode());
        }
    }


    public String getDdRegistrationDataXsd() {
        return ddRegistrationDataXsd;
    }

    public String getDiImportDataXsd() {
        return diImportDataXsd;
    }

    public URL getDdRegistrationDataXsdUrl() {
        return ddRegistrationDataXsdUrl;
    }

    public URL getDigInstImportXsdUrl() {
        return digInstImportXsdUrl;
    }
}
