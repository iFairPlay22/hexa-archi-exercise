package lunatech.services;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import lunatech.exceptions.UnauthorizedActionException;
import lunatech.security.Access;
import lunatech.security.Action;
import lunatech.security.Role;

import java.util.Collection;
import java.util.Map;

@ApplicationScoped
public class AuthService {
  @Context SecurityIdentity securityIdentity;

  /**
   * Retrieve the username of the connected user
   *
   * @return String name
   */
  public String userName() {
    if (securityIdentity.getPrincipal() == null) throw new RuntimeException("No authenticated user");
    return securityIdentity.getPrincipal().getName();
  }

  /**
   * Retrieve the connected user access, or unknown
   *
   * @return String name
   */
  public Access access(Action action) {
    return securityIdentity.getRoles()
        .stream()
        .findFirst()
        .map(role -> Role.fromString(role).getAccess().getOrDefault(action, Access.NONE))
        .orElse(Access.NONE);
  }

  public void failIfUnauthorizedAccess(Action action, String targetUserName) throws UnauthorizedActionException {
    switch (access(action)) {
      case GLOBAL -> {}
      case PERSONAL -> { if (!userName().equals(targetUserName)) throw new UnauthorizedActionException(); }
      case NONE -> throw new UnauthorizedActionException();
    }
  }
}
