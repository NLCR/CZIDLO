/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Martin Řehánek
 */
public interface ApplicationConfiguration {

    void initialize(InputStream properties) throws IOException;

    public Boolean isServerReadOnly();

    public Boolean isDevelMode();
}
