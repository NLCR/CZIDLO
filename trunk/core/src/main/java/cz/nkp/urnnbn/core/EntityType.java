/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

/**
 *
 * @author Martin Řehánek
 */
public enum EntityType {

    MONOGRAPH {

        @Override
        public String toString() {
            return "monograph";
        }
    },
    MONOGRAPH_VOLUME {

        @Override
        public String toString() {
            return "monographVolume";
        }
    },
    PERIODICAL {

        @Override
        public String toString() {
            return "periodical";
        }
    },
    PERIODICAL_VOLUME {

        @Override
        public String toString() {
            return "periodicalVolume";
        }
    },
    PERIODICAL_ISSUE {

        @Override
        public String toString() {
            return "periodicalIssue";
        }
    },
    THESIS {

        @Override
        public String toString() {
            return "thesis";
        }
    },
    ANALYTICAL {

        @Override
        public String toString() {
            return "analytical";
        }
    },
    OTHER {

        @Override
        public String toString() {
            return "otherEntity";
        }
    };
}
