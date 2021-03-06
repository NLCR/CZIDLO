/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnFinder {

    private static final Logger logger = Logger.getLogger(UrnNbnFinder.class.getName());
    private final DAOFactory factory;
    private final Registrar registrar;

    /**
     * This class find urn:nbn that is not yet assigned nor reserved
     * 
     * @param factory
     * @param registrar
     */
    public UrnNbnFinder(DAOFactory factory, Registrar registrar) {
        this.factory = factory;
        this.registrar = registrar;
    }

    /**
     * TODO: mozna optimalizovat, aby se po kazdem nalezenem urn:nbn nemuselo zapisovat do tabulky to by ale pripadne mohlo vyrobit nekonzistenci -
     * jeste rozmyslet
     * 
     * @return
     */
    UrnNbn findNewUrnNbn() throws DatabaseException {
        UrnNbnGenerator search = getSearchOrInsertNew();
        UrnNbn result = findFreeUrn(search.getLastDocumentCode());
        updateLastFound(search, result);
        return result;
    }

    private UrnNbnGenerator getSearchOrInsertNew() throws DatabaseException {
        try {
            return factory.urnSearchDao().getGeneratorByRegistrarId(registrar.getId());
        } catch (RecordNotFoundException ex) {
            // ok, so new search will be inserted
            try {
                logger.log(Level.SEVERE, "no urnNbnGenerator found for registrar with code {0}, inserting", registrar.getCode());
                UrnNbnGenerator search = new UrnNbnGenerator();
                search.setRegistrarId(registrar.getId());
                factory.urnSearchDao().insertGenerator(search);
                return search;
            } catch (AlreadyPresentException ex1) {
                logger.log(Level.SEVERE, null, ex1);
                return null;
            } catch (RecordNotFoundException ex1) {
                logger.log(Level.SEVERE, null, ex1);
                return null;
            }
        }
    }

    private void updateLastFound(UrnNbnGenerator search, UrnNbn urn) throws DatabaseException {
        try {
            search.setLastDocumentCode(urn.getDocumentCode());
            factory.urnSearchDao().updateGenerator(search);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, "failed to update last found urn " + urn.toString(), ex);
        }
    }

    private UrnNbn findFreeUrn(String lastFoundDocumentCode) throws DatabaseException {
        int step = 1;
        int iterationFromLastStepIncrease = 0;
        UrnNbnDocumentCode previous = UrnNbnDocumentCode.valueOf(lastFoundDocumentCode);
        while (true) {
            iterationFromLastStepIncrease++;
            UrnNbnDocumentCode tested = previous.getNext(step);
            if (isAvailable(tested)) {
                return new UrnNbn(registrar.getCode(), tested.toString(), null, null);
            } else {
                int newStep = increaseStep(step, iterationFromLastStepIncrease);
                if (newStep != step) {// step has been increased
                    iterationFromLastStepIncrease = 0;
                    step = newStep;
                }
                previous = tested;
            }
        }
    }

    private boolean isAvailable(UrnNbnDocumentCode tested) throws DatabaseException {
        try {
            factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), tested.toString());
            return false;
        } catch (RecordNotFoundException ex) {
            return true;
        }
    }

    // TODO: tohle jeste poradne rozmyslet.
    private int increaseStep(int step, int iterationFromLastStepIncrease) {
        if (iterationFromLastStepIncrease < 5) {
            return step;
        } else {
            return step * step;
        }
    }
}
