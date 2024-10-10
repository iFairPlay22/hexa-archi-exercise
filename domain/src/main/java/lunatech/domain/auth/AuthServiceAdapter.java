package lunatech.domain.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.auth.models.AuthModel;

import java.util.Optional;

@AllArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {
    private final AuthRepositoryPort authRepository;

    @Override
    public @NotNull Optional<AuthModel> login(
            @Valid @NotNull String username, @Valid @NotNull String password
    ) {
        return authRepository.findByUsername(username)
                .filter(auth -> auth.password().equals(password)); // TODO: SECURE
    }

    @Override
    public void canAccessTo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull AuthModel.Role.Action action
    ) throws UnauthorizedActionException {

        authRepository.findByUsername(actorUserName)
                .filter(auth ->
                        switch (auth.role().regarding(action)) {
                            case GLOBAL -> true;
                            case PERSONAL -> actorUserName.equals(targetUserName);
                            case NONE -> false;
                        }
                )
                .orElseThrow(() -> new UnauthorizedActionException(actorUserName, action));
    }

    @Override
    public void init() {
        AuthServicePort.users.forEach(authRepository::save);
    }
}
