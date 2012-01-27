/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.commons;

/**
 *
 * @author Martin Řehánek
 */
public class Xpath {

    private final String value;

    public Xpath(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
