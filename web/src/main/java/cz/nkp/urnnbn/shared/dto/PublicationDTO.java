package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class PublicationDTO implements Serializable {
	
	private static final long serialVersionUID = 6650405299148336858L;
	private String publisher;
	private String publicationPlace;
	private Integer publicationYear;

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublicationPlace() {
		return publicationPlace;
	}

	public void setPublicationPlace(String publicationPlace) {
		this.publicationPlace = publicationPlace;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}
	
	public boolean isEmpty(){
		return publisher == null && publicationPlace == null && publicationYear == null;
	}
}
