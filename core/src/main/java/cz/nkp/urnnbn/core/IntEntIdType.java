/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public enum IntEntIdType {

    ISBN {

        @Override
        public String toString() {
            return "isbn";
        }
    },
    CCNB {

        @Override
        public String toString() {
            return "ccnb";
        }
    },
    ISSN {

        @Override
        public String toString() {
            return "issn";
        }
    },
    PERIODICAL_VOLUME_NUMBER {

        @Override
        public String toString() {
            return "perVolume";
        }
    },
    PERIODICAL_ISSUE_NUMBER {

        @Override
        public String toString() {
            return "perIssue";
        }
    },
    OTHER {

        @Override
        public String toString() {
            return "otherId";
        }
    };
    //TODO: z enumu na vlastni tridu, aby byly mozne typy "OTHER:neco"

    //TODO: tohle asi nepatri sem, ale do presenteru
    public static List<IntEntIdType> searchableIds() {
        List<IntEntIdType> result = new ArrayList<IntEntIdType>();
        result.add(CCNB);
        result.add(ISBN);
        result.add(ISSN);
        return result;
    }

    public boolean isAllowedFor(EntityType type) {
        List<IntEntIdType> allowedTypes = idsAllowedFor(type);
        return allowedTypes.contains(this);
    }

    public List<IntEntIdType> idsAllowedFor(EntityType type) {
        List<IntEntIdType> result = new ArrayList<IntEntIdType>();
        result.add(CCNB);
        result.add(OTHER);
        switch (type) {
            case MONOGRAPH:
                result.add(ISBN);
                return result;
            case PERIODICAL:
                result.add(ISSN);
                return result;
            case PERIODICAL_VOLUME:
                result.add(ISSN);
                result.add(PERIODICAL_VOLUME_NUMBER);
                return result;
            case PERIODICAL_ISSUE:
                result.add(ISSN);
                result.add(PERIODICAL_VOLUME_NUMBER);
                result.add(PERIODICAL_ISSUE_NUMBER);
                return result;
            case THESIS:
                return result;
            case ANALYTICAL:
                result.add(ISBN);
                result.add(ISSN);
                return result;
            default:
                return result;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case ISBN:
                return "isbn";
            case CCNB:
                return "ccnb";
            case ISSN:
                return "issn";
            case PERIODICAL_VOLUME_NUMBER:
                return "volumeNumber";
            case PERIODICAL_ISSUE_NUMBER:
                return "issueNumber";
            case OTHER:
                return "otherId";
            default:
                return "";
        }
    }
}
