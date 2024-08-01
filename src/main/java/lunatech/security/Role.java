package lunatech.security;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.util.Map;

@Getter
public enum Role {
    /**
     * A regular user can access to everything
     */
    REGULAR(Map.of(Action.GET, Access.PERSONAL, Action.POST, Access.PERSONAL, Action.PUT, Access.PERSONAL, Action.DELETE, Access.PERSONAL)),
    /**
     * A regular user is limited to its own scope
     */
    ADMIN(Map.of(Action.GET, Access.GLOBAL, Action.POST, Access.GLOBAL, Action.PUT, Access.GLOBAL, Action.DELETE, Access.GLOBAL)),
    /**
     * An unknown user cannot do anything
     */
    UNKNOWN(Map.of());

    final Map<Action, Access> access;

    Role(@NotNull Map<Action, Access> access) {
        this.access = access;
    }

    // STRING <-> RoleEntity conversion
    public static class Names {
        public static final String REGULAR = "REGULAR";
        public static final String ADMIN = "ADMIN";
        public static final String UNKNOWN = "UNKNOWN";
    }

    /**
     * Returns the associated label, or unknown
     */
    public String toString() {
        return switch (this) {
            case REGULAR -> Names.REGULAR;
            case ADMIN -> Names.ADMIN;
            case UNKNOWN -> Names.UNKNOWN;
        };
    }

    /**
     * Retrieve a Role from a String
     * @param label Role name
     */
    public static Role fromString(@NotNull String label) {
        return switch (label) {
            case Names.REGULAR -> REGULAR;
            case Names.ADMIN -> ADMIN;
            default -> UNKNOWN;
        };
    }
}



