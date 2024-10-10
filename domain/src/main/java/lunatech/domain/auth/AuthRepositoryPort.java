package lunatech.domain.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.auth.models.AuthModel;

import java.util.Optional;

/**
 * Port that allows to store and retrieve users for authentication purposes
 */
public interface AuthRepositoryPort {

    /**
     * Retrieves an authentication entity given its username, without any side effects.
     *
     * @param username Username filter
     * @return The related authentication entity
     */
    @NotNull
    Optional<@NotNull AuthModel> findByUsername(@Valid @NotNull String username);

    /**
     * Creates or updates an authentication entity in the system
     *
     * @param user Auth user to save
     */
    void save(@Valid @NotNull AuthModel user);

}
