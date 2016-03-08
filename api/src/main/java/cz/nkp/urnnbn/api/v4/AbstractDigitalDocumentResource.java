package cz.nkp.urnnbn.api.v4;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.apiv4.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;

public abstract class AbstractDigitalDocumentResource extends ApiV4Resource {

    protected static final String HEADER_REFERER = "referer";
    protected static final String PARAM_WITH_DIG_INST = "digitalInstances";

    protected DigitalDocumentBuilder digitalDocumentsXmlBuilder(DigitalDocument digDoc, UrnNbn urnNbn, boolean withDigitalInstances) {
        return new DigitalDocumentBuilder(digDoc, urnNbn,//
                registrarScopeIdentifiersBuilder(digDoc.getId()),//
                withDigitalInstances ? digitalInstancesXmlBuilder(digDoc) : null,//
                registrarXmlBuilder(digDoc.getRegistrarId()), archiverXmlBuilder(digDoc),//
                intelectualEntityXmlBuilder(digDoc.getIntEntId()));
    }

    private DigitalInstancesBuilder digitalInstancesXmlBuilder(DigitalDocument digDoc) {
        List<DigitalInstance> instances = dataAccessService().digInstancesByDigDocId(digDoc.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return new DigitalInstancesBuilder(result);
    }

    private RegistrarBuilder registrarXmlBuilder(Long registrarId) {
        return new RegistrarBuilder(dataAccessService().registrarById(registrarId), null, null);
    }

    private ArchiverBuilder archiverXmlBuilder(DigitalDocument digDoc) {
        if (digDoc.getRegistrarId() == digDoc.getArchiverId()) {
            return null;
        } else {
            return new ArchiverBuilder(dataAccessService().archiverById(digDoc.getArchiverId()));
        }
    }

    private IntelectualEntityBuilder intelectualEntityXmlBuilder(long intEntId) {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
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
