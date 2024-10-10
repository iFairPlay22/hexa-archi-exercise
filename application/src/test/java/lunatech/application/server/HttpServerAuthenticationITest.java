package lunatech.application.server;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lunatech.domain.auth.AuthServicePort;
import lunatech.test.resources.lifecycle.mongo.WithMongoDbResourceManager;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithMongoDbResourceManager
public class HttpServerAuthenticationITest {
    @Inject
    AuthServicePort authServicePort;

    @Test
    public void usersCanLoginAfterStartup() {
        AuthServicePort.users.forEach(user ->
                assertThat(authServicePort.login(user.username(), user.password())).isEqualTo(Optional.of(user))
        );
    }
}
