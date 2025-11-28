package cz.nkp.urnnbn.czidlo_web_api.api;


import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

public class AuthenticatedUserPrincipal implements Principal {

    private final User user;
    private final List<Registrar> managedRegistrars;

    public AuthenticatedUserPrincipal(User user, List<Registrar> managedRegistrars) {
        this.user = user;
        this.managedRegistrars = managedRegistrars == null
                ? Collections.emptyList()
                : List.copyOf(managedRegistrars);
    }

    @Override
    public String getName() {
        return user.getLogin();
    }

    public User getUser() {
        return user;
    }

    public List<Registrar> getManagedRegistrars() {
        return managedRegistrars;
    }

    public boolean managesRegistrar(long registrarId) {
        return managedRegistrars.stream().anyMatch(r -> r.getId() == registrarId);
    }

    public boolean managesRegistrar(String registrarCode) {
        return managedRegistrars.stream().anyMatch(r -> r.getCode().toString().equals(registrarCode));
    }
}
