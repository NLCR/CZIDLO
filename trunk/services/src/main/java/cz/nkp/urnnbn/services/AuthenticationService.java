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
public interface AuthenticationService extends BusinessService {

    /**
     * TODO: implementace by mela logovat pokusy o prihlaseni
     * a rozlisovat, jestli je neexistujici ucet, nebo spatne heslo
     * @param user
     * @return instance of User fetched from database and null with user 
     * with provided login and password doesn't exist
     */
    public User autheticatedUserOrNull(User user);
}
