package cz.nkp.urnnbn.indexer.es.domain.searching;


import java.util.List;
import java.util.Map;

public class Searching {

    public Long id;

    public List<UrnNbn> urnnbn;
    public String entitytype;
    public String otheroriginator;

    public Map<String, List<String>> ieidentifiers;
    public Map<String, List<String>> rsidentifiers;
    public List<SourceDocument> sourcedocument;
    public List<Originator> originator;
    public List<Publication> publication;

    public static class UrnNbn {
        public String registrarcode;
        public String documentcode;
        public Boolean active;

        public String getUrnnbn() {
            if (registrarcode == null || documentcode == null) {
                return null;
            }
            return "urn:nbn:cz:" + registrarcode + "-" + documentcode;
        }
    }

    public static class SourceDocument {
        public String title;
        public String volumetitle;
        public String issuetitle;
        public String ccnb;
        public String isbn;
        public String issn;
        public String otherid;
    }

    public static class Originator {
        public String type;
        public String value;
    }

    public static class Publication {
        public String place;
        public String publisher;
        public Integer pyear;
    }

    public static class IeIdentifierTypes {
        public static final String TITLE = "TITLE";
        public static final String SUB_TITLE = "SUB_TITLE";
        public static final String VOLUME_TITLE = "VOLUME_TITLE";
        public static final String ISSUE_TITLE = "ISSUE_TITLE";
        public static final String ISBN = "ISBN";
        public static final String ISSN = "ISSN";
        public static final String CCNB = "CCNB";
        public static final String OTHER = "OTHER";

        public static List<String> getValidTypes() {
            return List.of(TITLE, SUB_TITLE, VOLUME_TITLE, ISSUE_TITLE, ISBN, ISSN, CCNB, OTHER);
        }
    }

    public static class OriginatorTypes {
        public static final String AUTHOR = "AUTHOR";
        public static final String CORPORATION = "CORPORATION";
        public static final String EVENT = "EVENT";

        public static List<String> getValidTypes() {
            return List.of(AUTHOR, CORPORATION, EVENT);
        }
    }
}
