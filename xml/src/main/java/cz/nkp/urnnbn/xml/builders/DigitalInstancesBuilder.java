/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstancesBuilder extends XmlBuilder {

    private final long count;
    private final List<DigitalInstanceBuilder> instanceBuilderList;

    public DigitalInstancesBuilder(long count) {
        this.count = count;
        this.instanceBuilderList = null;
    }

    public DigitalInstancesBuilder(List<DigitalInstanceBuilder> instancesBuilders) {
        this.count = instancesBuilders.size();
        this.instanceBuilderList = instancesBuilders;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("digitalInstances", RESOLVER);
        if (instanceBuilderList == null) {
            Element countEl = appendElement(root, "totalCount");
            countEl.appendChild(Long.toString(count));
        } else {
            for (DigitalInstanceBuilder builder : instanceBuilderList) {
                appendBuilderResultfNotNull(root, builder);
            }
        }
        return root;
    }
}
