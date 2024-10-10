package lunatech.domain.auth.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lunatech.test.resources.fixtures.AFixture;

public class AuthModelFixtures extends AFixture<AuthModel, AuthModelFixtures.Params> {

    @Override
    public @NotNull Params defaultParams() {
        return AuthModelFixtures.Params.builder().build();
    }

    @Override
    public @NotNull AuthModel one(@NotNull Params params) {
        return new AuthModel(
                params.username != null ? params.username : faker.funnyName().name(),
                params.password != null ? params.password : faker.code().asin(),
                params.role != null ?
                        params.role :
                        AuthModel.Role.values()[faker.random().nextInt(AuthModel.Role.values().length)]
        );
    }

    @Builder
    public static class Params {
        private final @NotNull String username;
        private final @NotNull String password;
        private final @NotNull AuthModel.Role role;
    }

    public static class Samples {

        public static final AuthModelFixtures AUTH_MODEL_FIXTURES = new AuthModelFixtures();

        public static final AuthModel ADMIN_AUTH_MODEL = AUTH_MODEL_FIXTURES.one(
                AuthModelFixtures.Params.builder().username("ADMIN").role(AuthModel.Role.ADMIN).build()
        );

        public static final AuthModel REGULAR_AUTH_MODEL = AUTH_MODEL_FIXTURES.one(
                AuthModelFixtures.Params.builder().username("REGULAR").role(AuthModel.Role.REGULAR).build()
        );

        public static final AuthModel UNKNOWN_AUTH_MODEL = AUTH_MODEL_FIXTURES.one(
                AuthModelFixtures.Params.builder().username("UNKNOWN").role(AuthModel.Role.UNKNOWN).build()
        );

    }
}
