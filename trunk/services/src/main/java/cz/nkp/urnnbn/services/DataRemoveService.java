/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.services.exceptions.DigRepIdNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataRemoveService {

    public void removeDigitalRepresentationIdentifiers(long digRepId) throws UnknownDigRepException;

    public void removeDigitalRepresentationId(long digRepId, DigRepIdType type) throws UnknownDigRepException, DigRepIdNotDefinedException;
}
