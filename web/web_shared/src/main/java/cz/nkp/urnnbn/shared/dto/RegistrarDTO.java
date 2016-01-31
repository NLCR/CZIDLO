package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

import cz.nkp.urnnbn.shared.charts.Registrar;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		RegistrarDTO other = (RegistrarDTO) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
