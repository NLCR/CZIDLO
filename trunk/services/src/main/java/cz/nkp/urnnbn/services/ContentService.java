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
public interface ContentService extends BusinessService {

    public void init();

    @Deprecated
    public Content getContentByNameAndLanguage(String name, String language) throws ContentNotFoundException;

    @Deprecated
    public void updateContent(Content content);
}
