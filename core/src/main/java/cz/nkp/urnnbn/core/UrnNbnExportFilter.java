package cz.nkp.urnnbn.core;

import java.util.List;

import org.joda.time.DateTime;

public class UrnNbnExportFilter {

	private final DateTime begin;
	private final DateTime end;
	private final List<String> registrars;
	private final List<String> entityTypes;
	private final Boolean missingCcnb;
	private final Boolean missingIssn;
	private final Boolean missingIsbn;
	private final Boolean returnActive;
	private final Boolean returnDeactivated;

	public UrnNbnExportFilter(DateTime begin, DateTime end, List<String> registrars, List<String> entityTypes, Boolean missingCcnb,
			Boolean missingIssn, Boolean missingIsbn, Boolean returnActive, Boolean returnDeactivated) {
		super();
		this.begin = begin;
		this.end = end;
		this.registrars = registrars;
		this.entityTypes = entityTypes;
		this.missingCcnb = missingCcnb;
		this.missingIssn = missingIssn;
		this.missingIsbn = missingIsbn;
		this.returnActive = returnActive;
		this.returnDeactivated = returnDeactivated;
	}

	public DateTime getBegin() {
		return begin;
	}

	public DateTime getEnd() {
		return end;
	}

	public List<String> getRegistrars() {
		return registrars;
	}

	public List<String> getEntityTypes() {
		return entityTypes;
	}

	public Boolean getMissingCcnb() {
		return missingCcnb;
	}

	public Boolean getMissingIssn() {
		return missingIssn;
	}

	public Boolean getMissingIsbn() {
		return missingIsbn;
	}

	public Boolean getReturnActive() {
		return returnActive;
	}

	public Boolean getReturnDeactivated() {
		return returnDeactivated;
	}

}
