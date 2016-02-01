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
package cz.nkp.urnnbn.xml.commons;

import java.io.IOException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

/**
 *
 * @author Martin Řehánek
 */
public class XsltXmlTransformer {

    private final XSLTransform transformer;

    /**
     *
     * @param xsltDocument
     *            XSLT document in string
     * @throws ParsingException
     * @throws XSLException
     * @throws IOException
     */
    public XsltXmlTransformer(String xsltDocument) throws ParsingException, XSLException, IOException {
        this(XOMUtils.loadDocumentWithoutValidation(xsltDocument));
    }

    /**
     *
     * @param xsltDocument
     *            XSLT document
     * @throws XSLException
     */
    public XsltXmlTransformer(Document xsltDocument) throws XSLException {
        this.transformer = new XSLTransform(xsltDocument);
    }

    /**
     * Transforms source xml document into another xml document/
     *
     * @param souceDoc
     *            source document
     * @return
     * @throws XSLException
     */
    public Document transform(Document souceDoc) throws XSLException {
        Nodes output = transformer.transform(souceDoc);
        return XSLTransform.toDocument(output);
    }

    /**
     *
     * @param souceDoc
     *            source document in string
     * @return
     * @throws XSLException
     */
    public Document transform(String souceDoc) throws XSLException, ParsingException, ValidityException, IOException {
        Builder builder = new Builder();
        Document doc = builder.build(souceDoc, null);
        return transform(doc);
    }
}
