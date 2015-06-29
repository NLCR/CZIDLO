/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 *
 * @author Martin Řehánek
 */
public class Identifier {

    private final UrnNbn urnNbn;
    public Identifier(UrnNbn urnNbn) {
        if (urnNbn == null) {
            throw new NullPointerException("urnNbn");
        }
        this.urnNbn = urnNbn;
    }

    public static Identifier instanceOf(String stringValue) {
        return new Identifier(UrnNbn.valueOf(stringValue));
    }
    
    @Override
    public String toString(){
        return urnNbn.toString();
    }

//
//    public UrnNbn getUrnNbn() {
//        return urnNbn;
//    }
//
}
