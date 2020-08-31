package cz.nkp.urnnbn.api.v6;

import cz.nkp.urnnbn.api.v6.json.*;
import cz.nkp.urnnbn.api.v6.json.ie.IntelectualEntityBuilderJson;
import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.xml.apiv6.builders.*;
import cz.nkp.urnnbn.xml.apiv6.builders.ie.IntelectualEntityBuilderXml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDigitalDocumentResource extends ApiV6Resource {

    protected static final String HEADER_REFERER = "referer";
    protected static final String PARAM_WITH_DIG_INST = "digitalInstances";

    protected DigitalDocumentBuilderXml digitalDocumentBuilderXml(DigitalDocument digDoc, UrnNbn urnNbn, boolean withDigitalInstances) {
        return new DigitalDocumentBuilderXml(digDoc, urnNbn,//
                registrarScopeIdentifiersBuilderXml(digDoc.getId()),//
                withDigitalInstances ? digitalInstancesBuilderXml(digDoc) : null,//
                registrarBuilderXml(digDoc.getRegistrarId()),//
                archiverBuilderXml(digDoc),//
                intelectualEntityBuilderXml(digDoc.getIntEntId()));
    }

    private DigitalInstancesBuilderXml digitalInstancesBuilderXml(DigitalDocument digDoc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digDoc.getId());
        List<DigitalInstanceBuilderXml> result = new ArrayList<DigitalInstanceBuilderXml>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilderXml builder = new DigitalInstanceBuilderXml(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilderXml(result);
    }

    private RegistrarBuilder registrarBuilderXml(Long registrarId) {
        return new RegistrarBuilder(dataAccessService().registrarById(registrarId), null, null);
    }

    private ArchiverBuilderXml archiverBuilderXml(DigitalDocument digDoc) {
        if (digDoc.getRegistrarId() == digDoc.getArchiverId()) {
            return null;
        } else {
            return new ArchiverBuilderXml(dataAccessService().archiverById(digDoc.getArchiverId()));
        }
    }

    private IntelectualEntityBuilderXml intelectualEntityBuilderXml(long intEntId) {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilderXml.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    protected DigitalDocumentBuilderJson digitalDocumentBuilderJson(DigitalDocument digDoc, UrnNbn urnNbn, boolean withDigitalInstances) {
        return new DigitalDocumentBuilderJson(digDoc, urnNbn,//
                registrarScopeIdentifiersBuilderJson(digDoc.getId()),//
                withDigitalInstances ? digitalInstancesBuilderJson(digDoc) : null,//
                registrarBuilderJson(digDoc.getRegistrarId()),//
                archiverBuilderJson(digDoc),//
                intelectualEntityBuilderJson(digDoc.getIntEntId()));
    }

    private DigitalInstancesBuilderJson digitalInstancesBuilderJson(DigitalDocument digDoc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digDoc.getId());
        List<DigitalInstanceBuilderJson> result = new ArrayList<>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilderJson builder = new DigitalInstanceBuilderJson(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilderJson(result);
    }

    private RegistrarBuilderJson registrarBuilderJson(Long registrarId) {
        return new RegistrarBuilderJson(dataAccessService().registrarById(registrarId), null, null);
    }

    private ArchiverBuilderJson archiverBuilderJson(DigitalDocument digDoc) {
        if (digDoc.getRegistrarId() == digDoc.getArchiverId()) {
            return null;
        } else {
            return new ArchiverBuilderJson(dataAccessService().archiverById(digDoc.getArchiverId()));
        }
    }

    private IntelectualEntityBuilderJson intelectualEntityBuilderJson(long intEntId) {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilderJson.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    protected URI getAvailableActiveDigitalInstanceOrNull(Long digDocId, String refererUrl) throws URISyntaxException {
        List<DigitalInstance> instancesAll = dataAccessService().digInstancesByDigDocId(digDocId);
        List<DigitalInstance> instancesMatchingReferer = filterActiveAndMatchingReferer(instancesAll, refererUrl);
        List<DigitalInstance> instancesSelected = !instancesMatchingReferer.isEmpty() ? instancesMatchingReferer : instancesAll;
        // select best DI with respect do accessRestriction
        DigitalInstance bestDi = null;
        for (DigitalInstance di : instancesSelected) {
            if (di.isActive()) {
                return new URI(di.getUrl());
            }
            if (bestDi == null) {
                bestDi = di;
            } else { //UNLIMITED_ACCESS > UNKNOWN > LIMITED_ACCESS
                switch (di.getAccessRestriction()) { //possibly weaker access restriction
                    case UNLIMITED_ACCESS:
                        bestDi = di;
                        break;
                    case UNKNOWN:
                        if (bestDi.getAccessRestriction() == AccessRestriction.LIMITED_ACCESS) {
                            bestDi = di;
                        }
                        break;
                }
            }
        }
        if (bestDi != null) {
            return new URI(bestDi.getUrl());
        } else {
            return null;
        }
    }

    private List<DigitalInstance> filterActiveAndMatchingReferer(List<DigitalInstance> allInstances, String refererUrl) {
        List<DigitalInstance> instances = new ArrayList<>();
        if (refererUrl != null && !refererUrl.isEmpty()) {
            // vsechny katalogy
            List<Catalog> catalogs = dataAccessService().catalogs();
            // vsechny katalogy, u kterych se shoduje prefix
            List<Catalog> matchingCatalogs = filterCatalogsByMatchingPrefix(catalogs, refererUrl);
            for (Catalog catalog : matchingCatalogs) {
                // digitalni knihovny vlastnene stejnym registratorem, jako katalog
                List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(catalog.getRegistrarId());
                for (DigitalLibrary library : libraries) {
                    // instance DD
                    for (DigitalInstance instance : allInstances) {
                        // instance je ve vhodne knihovne a je aktivni
                        if (instance.getLibraryId() == library.getId() && instance.isActive()) {
                            instances.add(instance);
                        }
                    }
                }
            }
        }
        return instances;
    }

    private List<Catalog> filterCatalogsByMatchingPrefix(List<Catalog> catalogs, String refererUrl) {
        // oprimization
        if (catalogs.isEmpty()) {
            return catalogs;
        }
        List<Catalog> result = new ArrayList<Catalog>();
        for (Catalog catalog : catalogs) {
            String prefix = catalog.getUrlPrefix();
            if (prefix != null && !prefix.isEmpty() && refererUrl.startsWith(prefix)) {
                result.add(catalog);
            }
        }
        return result;
    }

}
