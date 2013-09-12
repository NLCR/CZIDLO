package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;


//FIXME: move filter fields to separate class
public class SelectUrnNbnExport implements StatementWrapper {
	
	private DateTime begin;
	
	private DateTime end;
	
	private List<String> registrars;
	
	private String registrationMode;
	
	private List<String> entityTypes;
	
	private Boolean cnbAssigned = null;
	
	private Boolean issnAsigned = null;
	
	private Boolean isbnAssigned = null;
	
	private Boolean active = null;
	
	private static final String SELECT = 
			"SELECT * FROM (SELECT" +
			"   'urn:nbn:cz:' || un.registrarcode || '-' || un.documentcode AS urn, " +
			"   un.registrarcode AS registrar, " +
			"   dd.created AS reserved, " +
			"   dd.modified AS modified, " +
			"   ie.entitytype AS entitytype, " +
			"   EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='CCNB') AS cnb, " +
			"   EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='ISSN') AS issn, " +
			"   EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='ISBN') AS isbn, " +
			"   ident.idvalue AS title, " +
			"   un.active AS active, " +
			"   (SELECT COUNT(*) FROM digitalinstance WHERE digitaldocumentid=dd.id) AS digitalinstances " +
			"FROM urnnbn un " +
			"   JOIN digitaldocument dd ON dd.id = un.digitaldocumentid " +
			"   JOIN intelectualentity ie on ie.id = dd.intelectualentityid " +
			"   JOIN ieidentifier ident ON ident.intelectualentityid = ie.id AND ident.type = 'TITLE' " +
			" ) AS row " +
			"WHERE (%s);"
	;
	
	public SelectUrnNbnExport() {
	}

	public String preparedStatement() {
		//List<Object> placeholders = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		if (begin != null) {
			sb.append("row.reserved > ? AND ");
			//placeholders.add(DateTimeUtils.datetimeToTimestamp(begin));
		}
		if (end != null) {
			sb.append("row.reserved < ? AND ");
			//placeholders.add(DateTimeUtils.datetimeToTimestamp(end));
		}
		if (registrars != null && registrars.size() > 0) {
			StringBuilder inBody = new StringBuilder("?");
			if (registrars.size() > 1) {
				for (int i = 1; i != registrars.size(); i++) {
					inBody.append(", ?");
				}
			}
			sb.append(String.format("row.registrar in (%s) AND ", inBody.toString()));
		}
		if (entityTypes != null) {
			StringBuilder inBody = new StringBuilder("?");
			if (entityTypes.size() > 1) {
				for (int i = 1; i != entityTypes.size(); i++) {
					inBody.append(", ?");
				}
			}
			sb.append(String.format("row.entitytype in (%s) AND ", inBody.toString()));
		}
		if (cnbAssigned != null) {
			sb.append("row.cnb = ? AND ");
		}
		if (issnAsigned != null) {
			sb.append("row.issn = ? AND ");
		}
		if (isbnAssigned != null) {
			sb.append("row.isbn = ? AND ");
		}
		if (active != null){
			sb.append("row.active = ? AND ");
		}
		sb.append(" true");
		String result = String.format(SELECT, sb.toString());
		return result;
	}

	public void populate(PreparedStatement st) throws SyntaxException {
		int index = 1;
		try {
			if (begin != null) {
				st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(begin));
				index++;
			}
			if (end != null) {
				st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(end));
				index++;
			}
			if (registrars != null && registrars.size() > 0) {
				for (String registrar : registrars) {
					st.setString(index, registrar);
					index++;
				}
			}
			if (entityTypes != null) {
				for (String type : entityTypes) {
					st.setString(index, type);
					index++;
				}
			}
			if (cnbAssigned != null) {
				st.setBoolean(index, cnbAssigned);
				index++;
			}
			if (issnAsigned != null) {
				st.setBoolean(index, issnAsigned);
				index++;
			}
			if (isbnAssigned != null) {
				st.setBoolean(index, isbnAssigned);
				index++;
			}
			if (active != null){
				st.setBoolean(index, active);
				index++;
			}
		} catch (SQLException sqle) {
			throw new SyntaxException(sqle);
		}
	}

	public DateTime getBegin() {
		return begin;
	}

	public void setBegin(DateTime begin) {
		this.begin = begin;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}

	public List<String> getRegistrars() {
		return registrars;
	}

	public void setRegistrars(List<String> registrars) {
		this.registrars = registrars;
	}

	public String getRegistrationMode() {
		return registrationMode;
	}

	public void setRegistrationMode(String registrationMode) {
		this.registrationMode = registrationMode;
	}

	public List<String> getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(List<String> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public Boolean getCnbAssigned() {
		return cnbAssigned;
	}

	public void setCnbAssigned(Boolean cnbAssigned) {
		this.cnbAssigned = cnbAssigned;
	}

	public Boolean getIssnAsigned() {
		return issnAsigned;
	}

	public void setIssnAsigned(Boolean issnAsigned) {
		this.issnAsigned = issnAsigned;
	}

	public Boolean getIsbnAssigned() {
		return isbnAssigned;
	}

	public void setIsbnAssigned(Boolean isbnAssigned) {
		this.isbnAssigned = isbnAssigned;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
