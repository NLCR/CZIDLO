package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class RegistrarDTO extends ArchiverDTO implements Serializable {
	private static final long serialVersionUID = 1524026185893558475L;
	private String code;
	private boolean allowedToRegisterFreeUrnNbn;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isAllowedToRegisterFreeUrnNbn() {
		return allowedToRegisterFreeUrnNbn;
	}

	public void setAllowedToRegisterFreeUrnNbn(boolean allowedToRegisterFreeUrnNbn) {
		this.allowedToRegisterFreeUrnNbn = allowedToRegisterFreeUrnNbn;
	}
}
