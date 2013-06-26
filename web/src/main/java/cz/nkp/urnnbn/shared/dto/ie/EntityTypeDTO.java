package cz.nkp.urnnbn.shared.dto.ie;

public enum EntityTypeDTO {

	MONOGRAPH {
		public String toString() {
			return "monograph";
		}
	},
	MONOGRAPH_VOLUME {
		public String toString() {
			return "monographVolume";
		}
	},
	PERIODICAL {
		public String toString() {
			return "periodical";
		}
	},
	PERIODICAL_VOLUME {
		public String toString() {
			return "periodicalVolume";
		}
	},
	PERIODICAL_ISSUE {
		public String toString() {
			return "periodicalIssue";
		}
	},
	THESIS {
		public String toString() {
			return "thesis";
		}
	},
	ANALYTICAL {
		public String toString() {
			return "analytical";
		}
	},
	OTHER {
		public String toString() {
			return "otherEntity";
		}
	};
}
