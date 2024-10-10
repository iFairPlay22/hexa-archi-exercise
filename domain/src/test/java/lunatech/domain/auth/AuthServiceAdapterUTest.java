package lunatech.domain.auth;

import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.AuthServiceAdapter;
import lunatech.domain.auth.AuthServicePort;
import lunatech.domain.auth.models.AuthModel;
import lunatech.domain.mocks.AuthRepositoryMock;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static lunatech.domain.auth.models.AuthModelFixtures.Samples.AUTH_MODEL_FIXTURES;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthServiceAdapterUTest {

    private final AuthModel authModel = AUTH_MODEL_FIXTURES.one();

    private final AuthRepositoryPort authRepository = new AuthRepositoryMock(authModel);

    private final AuthServicePort authService = new AuthServiceAdapter(authRepository);

    @Nested
    class Login {

        @Test
        void findUserByUsernameWhenBadUsername() {
            assertThat(authService.login("???", "???")).isEqualTo(Optional.empty());
        }

        @Test
        void findUserByUsernameWhenBadPassword() {
            AuthModel otherAuthModel = AUTH_MODEL_FIXTURES.one();
            assertThat(authService.login(otherAuthModel.username(), "???")).isEqualTo(Optional.empty());
        }

        @Test
        void findUserByUsernameWhenValidCredentials() {
            assertThat(authService.login(authModel.username(), authModel.password())).isEqualTo(Optional.of(authModel));
        }

    }
}
