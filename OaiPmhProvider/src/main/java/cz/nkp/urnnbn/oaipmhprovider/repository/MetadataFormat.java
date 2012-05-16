/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import cz.nkp.urnnbn.oaipmhprovider.tools.dom4j.Namespaces;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public enum MetadataFormat {

    oai_dc {

        @Override
        public URL getSchemaUrl() {
            return url("http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        }

        @Override
        public String getNamespaceUri() {
            return Namespaces.oai_dc.getURI();
        }
    }, resolver {

        @Override
        public URL getSchemaUrl() {
            try {
                return new URL("http://TODO.com");
            } catch (MalformedURLException ex) {
                Logger.getLogger(MetadataFormat.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        @Override
        public String getNamespaceUri() {
            return Namespaces.resolver.getURI();
        }
    };

    public static MetadataFormat parseString(String string) {
        for (MetadataFormat format : MetadataFormat.values()) {
            if (format.toString().equals(string)) {
                return format;
            }
        }
        throw new IllegalArgumentException("no such metadata format '" + string + "'");
    }

    public abstract URL getSchemaUrl();

    public abstract String getNamespaceUri();

    protected URL url(String string) {
        try {
            return new URL(string);
        } catch (MalformedURLException ex) {
            //should never happen
            Logger.getLogger(MetadataFormat.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
