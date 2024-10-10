package lunatech.domain.auth.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public record AuthModel(

        @NotNull(message = "Username should be set")
        @Size(min = 3, max = 20, message = "Username length should be between 3 and 20 characters")
        String username,

        @NotNull(message = "Password should be set")
        @Size(min = 3, max = 20, message = "Password length should be between 3 and 20 characters")
        String password,

        @NotNull(message = "Role should be set")
        Role role

) {

    public enum Role {

        /**
         * A regular user can access to everything
         */
        REGULAR(new HashMap<>() {{
            put(Action.VISUALIZE, Access.PERSONAL);
            put(Action.CREATE, Access.PERSONAL);
            put(Action.UPDATE, Access.PERSONAL);
            put(Action.DELETE, Access.PERSONAL);
        }}),
        /**
         * A regular user is limited to its own scope
         */
        ADMIN(new HashMap<>() {{
            put(Action.VISUALIZE, Access.GLOBAL);
            put(Action.CREATE, Access.GLOBAL);
            put(Action.UPDATE, Access.GLOBAL);
            put(Action.DELETE, Access.GLOBAL);
        }}),
        /**
         * An unknown user cannot do anything
         */
        UNKNOWN(new HashMap<>());

        private final @NotNull Map<@NotNull Action, @NotNull Access> access;

        Role(@NotNull Map<@NotNull Action, @NotNull Access> access) {
            this.access = access;
        }

        public @NotNull Access regarding(@NotNull Action action) {
            return access.getOrDefault(action, Access.NONE);
        }

        // --------------------------------------------------------- //

        public enum Action {VISUALIZE, CREATE, UPDATE, DELETE}

        public enum Access {GLOBAL, PERSONAL, NONE}

        public static class Names {
            public final static String REGULAR = "REGULAR";
            public final static String ADMIN = "ADMIN";
            public final static String UNKNOWN = "UNKNOWN";
        }
    }

}
