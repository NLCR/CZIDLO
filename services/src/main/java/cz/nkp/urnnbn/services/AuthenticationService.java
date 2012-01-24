/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.User;

/**
 *
 * @author Martin Řehánek
 */
public interface AuthenticationService extends BusinessService{

    /**
     * TODO: implementace by mela logovat pokusy o prihlaseni
     * a rozlisovat, jestli je neexistujici ucet, nebo spatne heslo
     * @param user
     * @return true if user with this login and password exists
     */
    public boolean authenticate(User user);
}
