package lunatech.domain.auth.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lunatech.domain.auth.models.AuthModel;

@AllArgsConstructor
public final class UnauthorizedActionException extends Exception {

    private final @NotNull String username;
    private final @NotNull AuthModel.Role.Action action;

    @Override
    public String getMessage() {
        return String.format("Missing or insufficient permissions for user %s to perform the action %s.", action, username);
    }
}
