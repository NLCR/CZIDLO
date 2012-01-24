/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibrariesBuilder extends XmlBuilder {

    private final List<DigitalLibrary> libraryBuilderList;

    public DigitalLibrariesBuilder(List<DigitalLibrary> libraryBuilderList) {
        this.libraryBuilderList = libraryBuilderList;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalLibraries", RESOLVER);
        for (DigitalLibrary library : libraryBuilderList) {
            DigitalLibraryBuilder builder = new DigitalLibraryBuilder(library, null);
            appendBuilderResultfNotNull(root, builder);
        }
        return root;
    }
}
