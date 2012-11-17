package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class RegistrarDTO extends ArchiverDTO implements Serializable {

	private static final long serialVersionUID = -546318886531924354L;
	private String code;
	private boolean regModeByRegistrarAllowed;
	private boolean regModeByResolverAllowed;
	private boolean regModeByReservationAllowed;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isRegModeByRegistrarAllowed() {
		return regModeByRegistrarAllowed;
	}

	public void setRegModeByRegistrarAllowed(boolean regModeByRegistrarAllowed) {
		this.regModeByRegistrarAllowed = regModeByRegistrarAllowed;
	}

	public boolean isRegModeByResolverAllowed() {
		return regModeByResolverAllowed;
	}

	public void setRegModeByResolverAllowed(boolean regModeByResolverAllowed) {
		this.regModeByResolverAllowed = regModeByResolverAllowed;
	}

	public boolean isRegModeByReservationAllowed() {
		return regModeByReservationAllowed;
	}

	public void setRegModeByReservationAllowed(boolean regModeByReservationAllowed) {
		this.regModeByReservationAllowed = regModeByReservationAllowed;
	}
}
