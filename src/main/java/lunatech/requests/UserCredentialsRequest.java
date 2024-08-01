package lunatech.requests;


import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

/**
 * Utility class for user credentials and roles retrieval (panache query projection)
 */
@RegisterForReflection
public record UserCredentialsRequest(@NotNull String username, @NotNull String password, @NotNull String role) {}
