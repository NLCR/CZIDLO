/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.User;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DtoBuilder {

    private int urnPrefixCounter = 100;
    private int loginSuffix = 0;

    private int nextUrnPrefixSuffix() {
        return urnPrefixCounter++;
    }

    private int nextLoginSuffix() {
        return loginSuffix++;
    }

    private DateTime now() {
        return new DateTime();
    }

    public Archiver archiverWithoutId() {
        Archiver archiver = new Archiver();
        archiver.setName("MZK");
        archiver.setDescription("Moravska zemska knihovna");
        return archiver;
    }

    public Catalog CatalogueWithoutIdAndRegistrarId() {
        Catalog catalog = new Catalog();
        catalog.setName("Aleph");
        catalog.setDescription("katalog Aleph");
        catalog.setUrlPrefix("http://aleph.mzk.cz");
        return catalog;
    }

    public DigitalInstance digitalInstanceWithoutId() {
        DigitalInstance instance = new DigitalInstance();
        instance.setPublished(now());
        instance.setUrl("http://kramerius.mzk.cz/handle/uuid:123");
        return instance;
    }

    public DigitalLibrary digLibraryWithoutIdAndRegistrarId() {
        DigitalLibrary library = new DigitalLibrary();
        library.setName("Kramerius");
        library.setDescription("Kramerius MZK");
        library.setUrl("http://kramerius.mzk.cz");
        return library;
    }

    public DigitalDocument digDocWithoutIds() {
        DigitalDocument doc = new DigitalDocument();
        doc.setCreated(now());
        doc.setLastUpdated(now());
        return doc;
    }

    public DigDocIdentifier digDocIdentifierWithoutIds() {
        DigDocIdentifier id = new DigDocIdentifier();
        id.setType(DigDocIdType.valueOf("K4_pid"));
        id.setValue("uuid:123");
        return id;
    }

    public IntEntIdentifier intEntIdentifier(long entityDbId) {
        IntEntIdentifier result = new IntEntIdentifier();
        result.setIntEntDbId(entityDbId);
        result.setType(IntEntIdType.ISBN);
        result.setValue("80-7051-047-1");
        return result;
    }

    public IntelectualEntity intEntityWithoutId() {
        IntelectualEntity entity = new IntelectualEntity();
        entity.setEntityType(EntityType.MONOGRAPH);
        entity.setCreated(now());
        entity.setLastUpdated(now());
        entity.setTitle("Babička");
        entity.setAlternativeTitle("obrazy venkovského života");
        entity.setDigitalBorn(false);
        return entity;
    }

    public Originator originatorWithoutId() {
        Originator originator = new Originator();
        originator.setType(OriginType.CORPORATION);
        originator.setValue("IBM print");
        return originator;
    }

    public Publication publicationWithoutId() {
        Publication pub = new Publication();
        pub.setPlace("V Praze");
        pub.setPublisher("Československý spisovatel");
        pub.setYear(2011);
        return pub;
    }

    public Registrar registrarWithoutId() {
        Registrar registrar = new Registrar();
        registrar.setName("MZK");
        registrar.setDescription("Moravská zemská knihovna");
        registrar.setCode("BOA" + nextUrnPrefixSuffix());
        return registrar;
    }

    public SourceDocument sourceDocumentWithoutId() {
        SourceDocument doc = new SourceDocument();
        doc.setTitle("Lidové noviny");
        return doc;
    }

    public User userWithoutId() {
        User user = new User();
        user.setLogin("martin" + nextLoginSuffix());
        user.setPassword("heslo");
        user.setAdmin(false);
        user.setEmail("martin@somewhere.com");
        return user;
    }
}
