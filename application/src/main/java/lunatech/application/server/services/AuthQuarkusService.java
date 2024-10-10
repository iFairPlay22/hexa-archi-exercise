package lunatech.application.server.services;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Context;

@ApplicationScoped
public class AuthQuarkusService {
    @Context
    SecurityIdentity securityIdentity;

    /**
     * Retrieve the username of the connected user
     *
     * @return String name
     */
    public @NotNull String userName() {
        if (securityIdentity.getPrincipal() == null) throw new IllegalStateException("No authenticated user");
        return securityIdentity.getPrincipal().getName();
    }
}
