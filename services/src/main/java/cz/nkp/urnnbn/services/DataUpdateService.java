/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigRepException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;

/**
 *
 * @author Martin Řehánek
 */
public interface DataUpdateService extends BusinessService {

    public void updateDigRepIdentifier(DigRepIdentifier id) throws
            UnknownRegistrarException, UnknownDigRepException,
            IdentifierConflictException;
}
