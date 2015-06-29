/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class Parser {

    public static MetadataFormat parseMetadataPrefix(String metadataPrefix) throws OaiException {
        try {
            return MetadataFormat.parseString(metadataPrefix);
        } catch (Throwable e) {
            throw new OaiException(ErrorCode.cannotDisseminateFormat, "Invalid metadata prefix '" + metadataPrefix + "'");
        }
    }

    public static Identifier parseIdentifier(String idString) throws OaiException {
        try {
            return Identifier.instanceOf(idString);
        } catch (Throwable e) {
            throw new OaiException(ErrorCode.idDoesNotExist, "Invalid identifier '" + idString + "'");
        }
    }
}
