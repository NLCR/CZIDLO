/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport.validation;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Jan Rychtář
 */
public class ImportValidator {

    public static void validate(Document document) throws SAXException, ParserConfigurationException, ParsingException, ValidityException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        String schemaPath = "/home/hanis/prace/resolver/urnnbn-resolver-v2/legacyRecordsImport/src/main/java/cz/nkp/urnnbn/legacyrecordsimport/validation/import.xsd";
        factory.setSchema(schemaFactory.newSchema(
                new Source[]{new StreamSource(schemaPath)}));

        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new ImportErrorHandler());

        Builder builder = new Builder(reader);
        builder.build(document.toXML(), null);
    }
}
