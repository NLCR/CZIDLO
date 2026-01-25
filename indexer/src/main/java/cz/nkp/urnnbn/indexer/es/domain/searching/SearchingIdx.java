package cz.nkp.urnnbn.indexer.es.domain.searching;


import cz.nkp.urnnbn.indexer.es.domain.DomainIdx;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cz.nkp.urnnbn.indexer.es.domain.searching.Searching.IeIdentifierTypes.*;

public class SearchingIdx implements DomainIdx {
    // searching
    public Long id;
    public String entitytype;
    public String digitalborn;
    public String otheroriginator;

    // urnnbn
    public String urnnbn;
    public String documentcode;
    public String registrarcode;
    public Boolean active;
    public LocalDateTime registered;

    // ieidentifiers
    public String title;
    public String subtitle;
    public String volumetitle;
    public String issuetitle;

    //registrarscopeidentifiers
    public List<String> rsidkeyvalues;
    public List<String> rsidvalues;

    // sourcedocument
    public String sdtitle;
    public String sdvolumetitle;
    public String sdissuetitle;
    public String ccnb;
    public String isbn;
    public String issn;
    public String otherid;

    // originator
    public String originatortype;
    public String originatorvalue;

    //publication
    public String pubplace;
    public String publisher;
    public Integer pubyear;

    public static SearchingIdx fromDb(Searching searching) {
        SearchingIdx idx = new SearchingIdx();

        idx.id = searching.id;
        idx.entitytype = searching.entitytype;
        idx.digitalborn = searching.digitalborn;
        idx.otheroriginator = searching.otheroriginator;

        Searching.UrnNbn urnnbn = searching.urnnbn.getFirst();
        idx.urnnbn = urnnbn.getUrnnbn();
        idx.documentcode = urnnbn.documentcode;
        idx.registrarcode = urnnbn.registrarcode;
        idx.active = urnnbn.active;
        idx.registered = urnnbn.registered;

        Map<String, List<String>> rsidentifiers = searching.rsidentifiers;
        if (rsidentifiers != null) {
            //each item as $value
            idx.rsidvalues = rsidentifiers.values().stream()
                    .filter(list -> list != null && !list.isEmpty())
                    .map(list -> list.get(0))
                    .map(String::valueOf)
                    .toList();
            //each item as $key:$value
            idx.rsidkeyvalues = rsidentifiers.entrySet().stream()
                    .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                    .map(e -> e.getKey() + ":" + e.getValue().get(0))
                    .toList();
        }
        if (searching.sourcedocument != null) {
            Searching.SourceDocument sourceDocument = searching.sourcedocument.get(0);
            idx.sdtitle = sourceDocument.title;
            idx.sdvolumetitle = sourceDocument.volumetitle;
            idx.sdissuetitle = sourceDocument.issuetitle;
            idx.ccnb = sourceDocument.ccnb;
            idx.isbn = sourceDocument.isbn;
            idx.issn = sourceDocument.issn;
            idx.otherid = sourceDocument.otherid;
        }

        Map<String, List<String>> ieidentifiers = searching.ieidentifiers;
        if (ieidentifiers.containsKey(TITLE)) {
            idx.title = ieidentifiers.get(TITLE).getFirst();
        }
        if (ieidentifiers.containsKey(SUB_TITLE)) {
            idx.subtitle = ieidentifiers.get(SUB_TITLE).getFirst();
        }
        if (ieidentifiers.containsKey(ISSUE_TITLE)) {
            idx.issuetitle = ieidentifiers.get(ISSUE_TITLE).getFirst();
        }
        if (ieidentifiers.containsKey(VOLUME_TITLE)) {
            idx.volumetitle = ieidentifiers.get(VOLUME_TITLE).getFirst();
        }
        if (idx.ccnb == null && ieidentifiers.containsKey(CCNB)) {
            idx.ccnb = ieidentifiers.get(CCNB).getFirst();
        }
        if (idx.isbn == null && ieidentifiers.containsKey(ISBN)) {
            idx.isbn = ieidentifiers.get(ISBN).getFirst();
        }
        if (idx.issn == null && ieidentifiers.containsKey(ISSN)) {
            idx.issn = ieidentifiers.get(ISSN).getFirst();
        }

        if (searching.originator != null) {
            Searching.Originator originator = searching.originator.get(0);
            idx.originatortype = originator.type;
            idx.originatorvalue = originator.value;
        }
        if (searching.publication != null) {
            Searching.Publication publication = searching.publication.get(0);
            idx.pubplace = publication.place;
            idx.publisher = publication.publisher;
            idx.pubyear = publication.pyear;
        }

        return idx;
    }

    @Override
    public String toString() {
        return "Search{" +
                "documentcode='" + documentcode + '\'' +
                ", registrarcode='" + registrarcode + '\'' +
                ", entitytype='" + entitytype + '\'' +
                ", digitalborn=" + digitalborn +
                ", active=" + active +
                ", registered=" + registered +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", volumetitle='" + volumetitle + '\'' +
                ", issuetitle='" + issuetitle + '\'' +
                ", sdtitle='" + sdtitle + '\'' +
                ", sdvolumetitle='" + sdvolumetitle + '\'' +
                ", sdissuetitle='" + sdissuetitle + '\'' +
                ", ccnb='" + ccnb + '\'' +
                ", isbn='" + isbn + '\'' +
                ", issn='" + issn + '\'' +
                ", otherid='" + otherid + '\'' +
                ", " + originatortype + "=" + originatorvalue +
                ", otheroriginator='" + otheroriginator + '\'' +
                ", rsidvalues=" + rsidvalues +
                ", rsidkeyvalues=" + rsidkeyvalues +
                ", pubplace='" + pubplace + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pubyear=" + pubyear +
                '}';
    }

    @Override
    public String getId() {
        return Long.toString(id);
    }
}
