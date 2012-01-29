/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigitalRepresentationException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataRemoveService {

    public void removeDigitalRepresentationIdentifiers(long digRepId) throws UnknownDigitalRepresentationException;

    public void removeDigitalRepresentationId(long digRepId, DigRepIdType type) throws UnknownDigitalRepresentationException, DigRepIdNotDefinedException;
}
