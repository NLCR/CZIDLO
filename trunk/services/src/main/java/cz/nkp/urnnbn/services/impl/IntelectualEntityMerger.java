/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.DAOFactory;

/**
 *
 * @author Martin Řehánek
 */
public class IntelectualEntityMerger {

    private final DAOFactory factory;

    public IntelectualEntityMerger(DAOFactory factory) {
        this.factory = factory;
    }

    /**
     * @param ie
     * @return Intelectual entity that will be used instead of this intelectual entity
     * or null if no such entity is found
     */
    public IntelectualEntity getIntEntForMergingOrNull(IntelectualEntity ie) {
        return null;
        //TODO: implement
    }
}
