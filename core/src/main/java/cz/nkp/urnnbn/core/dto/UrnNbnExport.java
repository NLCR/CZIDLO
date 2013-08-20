package cz.nkp.urnnbn.core.dto;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;

public class UrnNbnExport {
	
	private String urn;
	
	private DateTime reserved;
	
	private DateTime modified;
	
	private UrnNbnRegistrationMode registrationMode;
	
	private String entityType;
	
	private boolean cnbAssigned;
	
	private boolean issnAssigned;
	
	private boolean isbnAssigned;
	
	private String title;
	
	private boolean active;
	
	private int numberOfDigitalInstances;

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public DateTime getReserved() {
		return reserved;
	}

	public void setReserved(DateTime reserved) {
		this.reserved = reserved;
	}

	public DateTime getModified() {
		return modified;
	}

	public void setModified(DateTime modified) {
		this.modified = modified;
	}

	public UrnNbnRegistrationMode getRegistrationMode() {
		return registrationMode;
	}

	public void setRegistrationMode(UrnNbnRegistrationMode registrationMode) {
		this.registrationMode = registrationMode;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public boolean isCnbAssigned() {
		return cnbAssigned;
	}

	public void setCnbAssigned(boolean cnbAssigned) {
		this.cnbAssigned = cnbAssigned;
	}

	public boolean isIssnAssigned() {
		return issnAssigned;
	}

	public void setIssnAssigned(boolean issnAsigned) {
		this.issnAssigned = issnAsigned;
	}

	public boolean isIsbnAssigned() {
		return isbnAssigned;
	}

	public void setIsbnAssigned(boolean isbnAssigned) {
		this.isbnAssigned = isbnAssigned;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getNumberOfDigitalInstances() {
		return numberOfDigitalInstances;
	}

	public void setNumberOfDigitalInstances(int numberOfDigitalInstances) {
		this.numberOfDigitalInstances = numberOfDigitalInstances;
	}

}
