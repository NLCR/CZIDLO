/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 * 
 * @author xrosecky
 */
public class ContentNotFoundException extends Exception {

    public ContentNotFoundException(String language, String name, RecordNotFoundException ex) {
        super(String.format("Content with language='%s' and name='%s' not found", language, name), ex);
    }

}
