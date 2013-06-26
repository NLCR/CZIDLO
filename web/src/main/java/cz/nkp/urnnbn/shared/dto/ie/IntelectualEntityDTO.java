package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;
import java.util.ArrayList;

import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;

public abstract class IntelectualEntityDTO implements Serializable {

	private static final long serialVersionUID = 993309152104731184L;
	private Long id;
	private ArrayList<DigitalDocumentDTO> documents;
	private String created;
	private String modified;
	private String documentType;
	private Boolean digitalBorn;
	private String otherOriginator;
	private PrimaryOriginatorDTO primaryOriginator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String lastUpdated) {
		this.modified = lastUpdated;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Boolean isDigitalBorn() {
		return digitalBorn;
	}

	public void setDigitalBorn(Boolean digitalBorn) {
		this.digitalBorn = digitalBorn;
	}

	public String getOtherOriginator() {
		return otherOriginator;
	}

	public void setOtherOriginator(String otherOriginator) {
		this.otherOriginator = otherOriginator;
	}

	public void setPrimaryOriginator(PrimaryOriginatorDTO primaryOriginator) {
		this.primaryOriginator = primaryOriginator;
	}

	public PrimaryOriginatorDTO getPrimaryOriginator() {
		return primaryOriginator;
	}

	public void setDocuments(ArrayList<DigitalDocumentDTO> documents) {
		this.documents = documents;
	}

	public PublicationDTO getPublication() {
		return null;
	}

	public SourceDocumentDTO getSourceDocument() {
		return null;
	}

	public ArrayList<DigitalDocumentDTO> getDocuments() {
		return documents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntelectualEntityDTO other = (IntelectualEntityDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
