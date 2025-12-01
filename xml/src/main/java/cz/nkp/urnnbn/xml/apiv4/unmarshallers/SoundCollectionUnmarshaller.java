package cz.nkp.urnnbn.xml.apiv4.unmarshallers;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.validation.CcnbEnhancer;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.validation.LimitedLengthEnhancer;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;

public class SoundCollectionUnmarshaller extends IntelectualEntityUnmarshaller {

    public SoundCollectionUnmarshaller(Element entityEl) {
        super(entityEl);
    }

    @Override
    public List<IntEntIdentifier> getIntEntIdentifiers() {
        List<IntEntIdentifier> result = new ArrayList<IntEntIdentifier>();
        Element titleInfoElement = selectSingleElementOrNull("titleInfo", entityEl);
        if (titleInfoElement == null) {
            logger.severe("missing element titleInfo");
        } else {
            appendId(result, identifierByElementName(titleInfoElement, "title", IntEntIdType.TITLE, true, new LimitedLengthEnhancer(100)));
            appendId(result, identifierByElementName(titleInfoElement, "subTitle", IntEntIdType.SUB_TITLE, false, new LimitedLengthEnhancer(200)));
        }
        appendId(result, identifierByElementName(entityEl, "ccnb", IntEntIdType.CCNB, false, new CcnbEnhancer()));
        appendId(result, identifierByElementName(entityEl, "otherId", IntEntIdType.OTHER, false, new LimitedLengthEnhancer(50)));
        return result;
    }
}
