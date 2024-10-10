package lunatech.domain.mocks;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.models.AuthModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthRepositoryMock implements AuthRepositoryPort {
    private final @NotNull List<@NotNull AuthModel> authEntities;

    public AuthRepositoryMock(@NotNull AuthModel... authEntities) {
        this.authEntities = new ArrayList<>(List.of(authEntities));
    }

    @Override
    public Optional<AuthModel> findByUsername(String username) {
        return authEntities.stream().filter(a -> a.username().equals(username)).findFirst();
    }

    @Override
    public void save(AuthModel user) {
        authEntities.removeIf(a -> a.username().equals(user.username()));
        authEntities.add(user);
    }
}
