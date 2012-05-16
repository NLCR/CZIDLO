/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository.impl;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnIdentifier  {

    private final UrnNbn urnNbn;

    public static UrnNbnIdentifier valueOf(String stringValue) {
        return new UrnNbnIdentifier(UrnNbn.valueOf(stringValue));
    }

    public UrnNbnIdentifier(UrnNbn urnNbn) {
        if (urnNbn == null){
            throw new NullPointerException("urnNbn");
        }
        this.urnNbn = urnNbn;
    }

    @Override
    public String toString() {
        return urnNbn.toString();
    }
}
