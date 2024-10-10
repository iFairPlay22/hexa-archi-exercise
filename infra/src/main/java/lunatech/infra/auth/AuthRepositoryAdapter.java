package lunatech.infra.auth;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.models.AuthModel;
import lunatech.infra.auth.entities.AuthEntity;

import java.util.Optional;

public class AuthRepositoryAdapter implements AuthRepositoryPort, PanacheMongoRepository<AuthEntity> {

    @Override
    public @NotNull Optional<@NotNull AuthModel> findByUsername(@Valid @NotNull String userName) {
        return stream("_id", userName)
                .map(AuthEntity::to)
                .findFirst();
    }

    @Override
    public void save(@Valid @NotNull AuthModel auth) {
        persistOrUpdate(AuthEntity.from(auth));
    }
}
