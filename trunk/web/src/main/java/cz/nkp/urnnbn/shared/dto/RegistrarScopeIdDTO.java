package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class RegistrarScopeIdDTO implements Serializable{
	
	private static final long serialVersionUID = 7712499256636990045L;
	private String type;
	private String value;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
