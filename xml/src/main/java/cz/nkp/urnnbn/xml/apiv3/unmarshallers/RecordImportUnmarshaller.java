/*
 * Copyright (C) 2011, 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.xml.apiv3.unmarshallers;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Xpath;

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

    static {// Xpath cache initialization
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
     * @return list of registrar-scope identifiers, never null
     */
    public List<RegistrarScopeIdentifier> getRegistrarScopeIdentifiers() {
        return digDocUnmarshaller.getRegistrarScopeIdentifiers();
    }

    /**
     * 
     * @return archiver id or null
     */
    public Long getArchiverId() {
        return digDocUnmarshaller.getArchiverId();
    }

    /**
     * 
     * @return UrnNbn or null
     */
    public UrnNbn getUrnNbn() {
        return digDocUnmarshaller.getUrnNbn();
    }

    public List<UrnNbnWithStatus> getPredecessors() {
        return digDocUnmarshaller.getPredecessors();
    }
}
