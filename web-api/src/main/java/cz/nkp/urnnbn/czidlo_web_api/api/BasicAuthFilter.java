package cz.nkp.urnnbn.czidlo_web_api.api;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.AuthenticationService;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");

        // anonymní uživatel – nepovinná autentizace
        if (authHeader == null || authHeader.isBlank()) {
            setAnonymousContext(requestContext);
            return;
        }

        if (!authHeader.startsWith("Basic ")) {
            abortUnauthorized(requestContext, "Unsupported authorization scheme");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials;
        try {
            credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            abortUnauthorized(requestContext, "Cannot decode credentials");
            return;
        }

        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            abortUnauthorized(requestContext, "Invalid credentials format");
            return;
        }

        String login = parts[0];
        String password = parts[1];

        User user = Services.instanceOf().authenticationService().autheticatedUserOrNull(login, password);
        if (user == null) {
            abortUnauthorized(requestContext, "Invalid username or password");
            return;
        }

        List<Registrar> managed = null;
        try {
            managed = Services.instanceOf().dataAccessService().registrarsManagedByUser(user.getId(), user.getLogin());
        } catch (UnknownUserException | AccessException e) {
            throw new RuntimeException(e);
        }
        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(user, managed);

        SecurityContext original = requestContext.getSecurityContext();
        SecurityContext customCtx = new SecurityContext() {
            @Override
            public java.security.Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String role) {
                if ("ADMIN".equalsIgnoreCase(role)) {
                    return user.isAdmin();
                }
                return false;
            }

            @Override
            public boolean isSecure() {
                return original != null && original.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.BASIC_AUTH;
            }
        };

        requestContext.setSecurityContext(customCtx);
    }

    private void setAnonymousContext(ContainerRequestContext ctx) {
        SecurityContext original = ctx.getSecurityContext();
        SecurityContext anon = new SecurityContext() {
            @Override
            public java.security.Principal getUserPrincipal() {
                return null; // anonym
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return original != null && original.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        };
        ctx.setSecurityContext(anon);
    }

    private void abortUnauthorized(ContainerRequestContext ctx, String message) {
        ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate", "Basic realm=\"CZIDLO Web API\"")
                        .entity(message)
                        .build()
        );
    }
}
