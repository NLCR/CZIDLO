/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class MonographVolumeUnmarshaller extends IntelectualEntityUnmarshaller {

    public MonographVolumeUnmarshaller(Element entityEl) {
        super(entityEl);
    }

    @Override
    public List<IntEntIdentifier> getIntEntIdentifiers() {
        List<IntEntIdentifier> result = new ArrayList<IntEntIdentifier>();
        Element titleInfoElement = selectSingleElementOrNull("titleInfo", entityEl);
        if (titleInfoElement == null) {
            logger.severe("missing element titleInfo");
        } else {
            appendId(result, identifierByElementName(titleInfoElement, "monographTitle", IntEntIdType.TITLE, true));
            appendId(result, identifierByElementName(titleInfoElement, "volumeTitle", IntEntIdType.VOLUME_TITLE, true));
        }
        appendId(result, identifierByElementName(entityEl, "ccnb", IntEntIdType.CCNB, false));
        appendId(result, identifierByElementName(entityEl, "isbn", IntEntIdType.ISBN, false));
        appendId(result, identifierByElementName(entityEl, "otherId", IntEntIdType.OTHER, false));
        return result;
    }
}
