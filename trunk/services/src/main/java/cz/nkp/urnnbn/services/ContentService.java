/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.services.exceptions.ContentNotFoundException;

/**
 *
 * @author xrosecky
 */
public interface ContentService {
    
    public void init();
    
    public Content getContentByNameAndLanguage(String name, String language) throws ContentNotFoundException;
    
    public void updateContent(Content content);
    
}
