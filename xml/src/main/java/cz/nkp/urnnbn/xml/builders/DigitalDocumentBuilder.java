/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.List;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentBuilder extends XmlBuilder {

    private final DigitalRepresentation rep;
    private final UrnNbn urn;
    private final DigitalDocumentIdentifiersBuilder identifiersBuilder;
    private final List<DigitalInstanceBuilder> instanceBuilderList;
    private final RegistrarBuilder registrarBuilder;
    private final ArchiverBuilder archiverBuilder;
    private final IntelectualEntityBuilder entityBuilder;

    public DigitalDocumentBuilder(DigitalRepresentation rep, UrnNbn urn, DigitalDocumentIdentifiersBuilder identifiersBuilder, List<DigitalInstanceBuilder> instanceBuilders, RegistrarBuilder registrarBuilder, ArchiverBuilder archiverBuilder, IntelectualEntityBuilder entityBuilder) {
        this.rep = rep;
        this.urn = urn;
        this.identifiersBuilder = identifiersBuilder;
        this.instanceBuilderList = instanceBuilders;
        this.registrarBuilder = registrarBuilder;
        this.archiverBuilder = archiverBuilder;
        this.entityBuilder = entityBuilder;
    }

    public Element buildRootElement() {
        Element root = new Element("digitalRepresentation", RESOLVER);
        appendIdentifierElement(root, "INTERNAL", rep.getId());
        appendIdentifierElement(root, "URN:NBN", urn);
        if (identifiersBuilder != null) {
            appendBuilderResultfNotNull(root, identifiersBuilder);
        }
        appendElementWithContentIfNotNull(root, rep.getAccessibility(), "accessibility");
        appendElementWithContentIfNotNull(root, rep.getColorDepth(), "colorDepth");
        appendElementWithContentIfNotNull(root, rep.getCreated(), "created");
        appendElementWithContentIfNotNull(root, rep.getExtent(), "extent");
        appendElementWithContentIfNotNull(root, rep.getFinancedFrom(), "financed");
        appendElementWithContentIfNotNull(root, rep.getFormat(), "format");
        appendElementWithContentIfNotNull(root, rep.getLastUpdated(), "lastUpdated");
        appendElementWithContentIfNotNull(root, rep.getResolution(), "resolution");
        appendBuilderResultfNotNull(root, registrarBuilder);
        appendBuilderResultfNotNull(root, archiverBuilder);
        appendBuilderResultfNotNull(root, entityBuilder);
        if (instanceBuilderList != null) {
            for (DigitalInstanceBuilder builder : instanceBuilderList) {
                appendBuilderResultfNotNull(root, builder);
            }
        }
        return root;
    }
}
