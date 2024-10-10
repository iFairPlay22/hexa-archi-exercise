package lunatech.domain.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.auth.models.AuthModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Port that allows to send requests to the domain, related to authentication management
 */
public interface AuthServicePort {

    /**
     * List of users that can be used for authentication
     */
    List<AuthModel> users = new ArrayList<>() {{
        add(new AuthModel("Nicolas", "pwd", AuthModel.Role.ADMIN)); // TODO: SECURE
        add(new AuthModel("Ewen", "pwd", AuthModel.Role.REGULAR)); // TODO: SECURE
        add(new AuthModel("Sebastien", "pwd", AuthModel.Role.REGULAR)); // TODO: SECURE
    }};

    /**
     * Try to log in a user given some credentials
     *
     * @param username Username
     * @param password Password
     * @return The user data in an option if the authentication succeeded, an empty option else
     */
    @NotNull
    Optional<AuthModel> login(@Valid @NotNull String username, @Valid @NotNull String password);

    /**
     * Throws an UnauthorizedActionException if the actor does not have the required access to perform the specified action on the target user.
     *
     * @param actorUserName  username of the user performing the action
     * @param targetUserName username of the user on which the action is performed
     * @param action         the action to be performed
     * @throws UnauthorizedActionException if the actor does not have the required access
     */
    void canAccessTo(@Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull AuthModel.Role.Action action) throws UnauthorizedActionException;

    /**
     * Initialization of the repository
     */
    void init();

}
