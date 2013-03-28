package cz.nkp.urnnbn.server.services;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public abstract class AbstractService extends RemoteServiceServlet {

	private static final long serialVersionUID = -428423808207398637L;
	final DataAccessService readService;
	final DataRemoveService deleteService;
	final DataImportService createService;
	final DataUpdateService updateService;

	public AbstractService() {
		readService = Services.instanceOf().dataAccessService();
		deleteService = Services.instanceOf().dataRemoveService();
		createService = Services.instanceOf().dataImportService();
		updateService = Services.instanceOf().dataUpdateService();
	}

	String getUserLogin() {
		UserDTO user = getActiveUser();
		if (user.getRole() == ROLE.USER) {
			// TODO: log as severe
			throw new RuntimeException("unauthrized operation attempt detected");
		}
		return user.getLogin();
	}

	UserDTO getActiveUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return notAuthenticated();
		} else {
			String login = (String) authentication.getPrincipal();
			if (login.equals("anonymousUser")) {
				return notAuthenticated();
			} else {
				ROLE role = getRole(authentication);
				if (role == null) {
					System.err.println("WARNING: no role for logged user " + login);
					return notAuthenticated();
				} else {
					UserDTO result = new UserDTO();
					result.setLogin(login);
					result.setRole(role);
					return result;
				}
			}
		}
	}

	private UserDTO notAuthenticated() {
		UserDTO result = new UserDTO();
		result.setRole(ROLE.USER);
		return result;
	}

	private ROLE getRole(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		if (authorities.isEmpty()) {
			return null;
		} else {
			// only single item in collection
			for (GrantedAuthority authority : authorities) {
				return toRole(authority.getAuthority());
			}
			// cannot happen
			return null;
		}
	}

	private ROLE toRole(String authority) {
		if ("ROLE_ADMIN".equals(authority)) {
			return ROLE.SUPER_ADMIN;
		} else if ("ROLE_USER".equals(authority)) {
			return ROLE.ADMIN;
		} else {
			return ROLE.USER;
		}
	}

}
