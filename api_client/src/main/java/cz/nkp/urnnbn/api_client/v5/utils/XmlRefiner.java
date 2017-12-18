package cz.nkp.urnnbn.api_client.v5.utils;

import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Created by Martin Řehánek on 30.10.17.
 */
public abstract class XmlRefiner {

    void cutContentIfToLongRemoveElementIfToShort(Element parentElement, String elementName, int contentMaxLength, int contentMinLength) {
        Element el = parentElement.getFirstChildElement(elementName, CzidloApiConnector.CZIDLO_NAMESPACE);
        if (el != null) {
            String value = el.getValue();
            if (value.length() > contentMaxLength) {
                el.removeChildren();
                el.appendChild(value.substring(0, contentMaxLength));
            }
            if (value.length() < contentMinLength) {
                parentElement.removeChild(el);
            }
        }
    }

    boolean removeElementIfContentNoMatch(Element parentElement, String elementName, String regex) {
        Element el = parentElement.getFirstChildElement(elementName, CzidloApiConnector.CZIDLO_NAMESPACE);
        if (el != null) {
            String value = el.getValue();
            if (!value.matches(regex)) {
                parentElement.removeChild(el);
                return true;
            }
        }
        return false;
    }

    boolean removeElementIfContentNoMatch(Element parentElement, Element element, String regex) {
        if (element != null) {
            String value = element.getValue();
            if (!value.matches(regex)) {
                parentElement.removeChild(element);
                return true;
            }
        }
        return false;
    }

    boolean removeAttributeIfValueNoMatch(Element parentElement, String attributeName, String regex) {
        Attribute attribute = parentElement.getAttribute(attributeName);
        if (attribute != null) {
            String value = attribute.getValue();
            if (!value.matches(regex)) {
                parentElement.removeAttribute(attribute);
                return true;
            }
        }
        return false;
    }
}
