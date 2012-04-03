/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public interface IdentifiableWithDatestamps extends IdentifiableByLongAttribute {

    public DateTime getCreated();

    public DateTime getModified();
}
