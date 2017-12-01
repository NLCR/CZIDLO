package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.v5.json.*;
import cz.nkp.urnnbn.api.v5.json.ie.IntelectualEntityBuilderJson;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.xml.apiv5.builders.*;
import cz.nkp.urnnbn.xml.apiv5.builders.ie.IntelectualEntityBuilderXml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDigitalDocumentResource extends ApiV5Resource {

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
        List<DigitalInstanceBuilderJson> result = new ArrayList<DigitalInstanceBuilderJson>(instances.size());
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

    protected URI getAvailableActiveDigitalInstanceOrNull(Long digDocId, ResponseFormat format, String refererUrl) throws URISyntaxException {
        List<DigitalInstance> allDigitalInstanceds = dataAccessService().digInstancesByDigDocId(digDocId);
        DigitalInstance instanceByReferer = getDigitalInstanceByRefererOrNull(allDigitalInstanceds, refererUrl);
        // LOGGER.info("by referer:" + instanceByReferer);
        if (instanceByReferer != null) { // prefered uri found
            return new URI(instanceByReferer.getUrl());
        } else { // return any uri
            // LOGGER.info("all instances: " + allDigitalInstanceds.size());
            for (DigitalInstance instance : allDigitalInstanceds) {
                // LOGGER.info(instance.toString());
                if (instance.isActive()) {
                    return new URI(instance.getUrl());
                }
            }
        }
        return null;
    }

    private DigitalInstance getDigitalInstanceByRefererOrNull(List<DigitalInstance> allInstances, String refererUrl) {
        if (refererUrl != null && !refererUrl.isEmpty()) {
            // vsechny katalogy
            List<Catalog> catalogs = dataAccessService().catalogs();
            // vsechny katalogy, u kterych se shoduje prefix
            List<Catalog> matching = filterCatalogsByMatchingPrefix(catalogs, refererUrl);
            for (Catalog catalog : matching) {
                // digitalni knihovny vlastnene stejnym registratorem, jako katalog
                List<DigitalLibrary> libraries = dataAccessService().librariesByRegistrarId(catalog.getRegistrarId());
                for (DigitalLibrary library : libraries) {
                    // instance DD
                    for (DigitalInstance instance : allInstances) {
                        // instance je ve vhodne knihovne a je aktivni
                        if (instance.getLibraryId() == library.getId() && instance.isActive()) {
                            return instance;
                        }
                    }
                }
            }
        }
        return null;
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
