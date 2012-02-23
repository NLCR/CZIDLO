/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import java.util.EnumMap;
import nu.xom.Document;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Xpath;
import java.util.List;
import java.util.Map;
import nu.xom.Element;
import nu.xom.Node;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImportUnmarshaller extends Unmarshaller {

    private final Document doc;
    private final DigitalDocumentUnmarshaller digDocUnmarshaller;
    private final IntelectualEntityUnmarshaller intEntUnmarshaller;
    private static final Xpath DIG_REP_XPATH;
    private static final Map<EntityType, Xpath> ENTITY_XPATH_CACHE = new EnumMap<EntityType, Xpath>(EntityType.class);

    static {//Xpath cache initialization
        DIG_REP_XPATH = new Xpath('/' + prefixed("import") + '/' + prefixed("digitalDocument"));
        for (EntityType type : EntityType.values()) {
            Xpath xpath = new Xpath('/' + prefixed("import") + '/' + prefixed(type.toString()));
            ENTITY_XPATH_CACHE.put(type, xpath);
        }
    }

    public RecordImportUnmarshaller(Document doc) {
        this.doc = doc;
        Element entityEl = intelectualEntityElement();
        intEntUnmarshaller = IntelectualEntityUnmarshaller.instanceOf(entityEl);
        Element digitalDocumentEl = (Element) selectSingleElementOrNullFromdoc(DIG_REP_XPATH);
        digDocUnmarshaller = new DigitalDocumentUnmarshaller(digitalDocumentEl);
    }

    private Element intelectualEntityElement() {
        for (EntityType type : EntityType.values()) {
            Node node = selectSingleElementOrNullFromdoc(ENTITY_XPATH_CACHE.get(type));
            if (node != null) {
                return (Element) node;
            }
        }
        throw new RuntimeException("no intelectual entity element found");
    }

    private Element selectSingleElementOrNullFromdoc(Xpath xpath) {
        return (Element) selectSingleNodeOrNull(xpath, doc);
    }

    /**
     * 
     * @return IntelectualEntity object, never null
     */
    public IntelectualEntity getIntelectualEntity() {
        return intEntUnmarshaller.getIntelectualEntity();
    }

    /**
     * 
     * @return Publication object or null
     */
    public Publication getPublication() {
        return intEntUnmarshaller.getPublication();
    }

    /**
     * 
     * @return Originator object or null
     */
    public Originator getOriginator() {
        return intEntUnmarshaller.getOriginator();
    }

    /**
     * 
     * @return SourceDocument object or null
     */
    public SourceDocument getSourceDocument() {
        return intEntUnmarshaller.getSourceDocument();
    }

    /**
     * 
     * @return list of intelectual entity identifiers, never null
     */
    public List<IntEntIdentifier> getIntEntIdentifiers() {
        return intEntUnmarshaller.getIntEntIdentifiers();
    }

    /**
     * 
     * @return DigitalDocument object, never null
     */
    public DigitalDocument getDigitalDocument() {
        return digDocUnmarshaller.getDigitalDocument();
    }

    /**
     * 
     * @return list of digital document identifiers, never null
     */
    public List<DigDocIdentifier> getDigRepIdentifiers() {
        return digDocUnmarshaller.getDigDocIdentifiers();
    }

    /**
     * 
     * @return UrnNbn or null
     */
    public UrnNbn getUrnNbn() {
        return digDocUnmarshaller.getUrnNbn();
    }

    /**
     * 
     * @return archiver id or null
     */
    public Long getArchiverId() {
        return digDocUnmarshaller.getArchiverId();
    }
}
