package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class PrimaryOriginatorDTO implements Serializable {

	private static final long serialVersionUID = -2695009483378879817L;

	private String value;
	private PrimaryOriginatorType type;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PrimaryOriginatorType getType() {
		return type;
	}

	public void setType(PrimaryOriginatorType type) {
		this.type = type;
	}

}
