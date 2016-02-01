package cz.nkp.urnnbn.server.services;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.StatisticService;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;
import cz.nkp.urnnbn.shared.exceptions.AuthorizationException;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

public abstract class AbstractService extends RemoteServiceServlet {

    private static final long serialVersionUID = -428423808207398637L;
    final DataImportService createService;
    final DataAccessService readService;
    final DataUpdateService updateService;
    final DataRemoveService deleteService;
    final StatisticService statisticService;

    public AbstractService() {
        createService = Services.instanceOf().dataImportService();
        readService = Services.instanceOf().dataAccessService();
        updateService = Services.instanceOf().dataUpdateService();
        deleteService = Services.instanceOf().dataRemoveService();
        statisticService = Services.instanceOf().statisticService();
    }

    protected String getUserLogin() throws SessionExpirationException {
        UserDTO user = getActiveUser();
        if (user.getRole() == ROLE.USER) {
            throw new SessionExpirationException();
        }
        return user.getLogin();
    }

    protected void checkUserIsAdmin() throws AuthorizationException, SessionExpirationException {
        UserDTO user = getActiveUser();
        if (user.getRole() == ROLE.USER) {
            throw new SessionExpirationException();
        } else if (user.getRole() != ROLE.SUPER_ADMIN) {
            throw new AuthorizationException("unauthorized operation attempt detected (allowed only for admins)");
        }
    }

    protected UserDTO getActiveUser() {
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

    public void checkNotReadOnlyMode() {
        if (WebModuleConfiguration.instanceOf().isServerReadOnly()) {
            throw new RuntimeException("Not allowed in read-only mode");
        }
    }

}
