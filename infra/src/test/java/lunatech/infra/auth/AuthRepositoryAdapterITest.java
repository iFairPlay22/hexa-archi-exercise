package lunatech.infra.auth;

import io.quarkus.test.junit.QuarkusTest;
import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.models.AuthModel;
import lunatech.domain.auth.models.AuthModelFixtures;
import lunatech.infra.auth.entities.AuthEntity;
import lunatech.test.resources.lifecycle.mongo.WithMongoDbResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;

import static lunatech.domain.auth.models.AuthModelFixtures.Samples.AUTH_MODEL_FIXTURES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
@WithMongoDbResourceManager
public class AuthRepositoryAdapterITest {

    private static final AuthRepositoryPort repository = new AuthRepositoryAdapter();

    private static List<Arguments> authModels() {
        return AUTH_MODEL_FIXTURES.many(10, AuthModelFixtures.Params.builder().build()).stream().map(Arguments::of).toList();
    }

    @BeforeEach
    void beforeEach() {
        AuthEntity.deleteAll();
    }

    @Nested
    class FindByUserName {

        @ParameterizedTest
        @MethodSource(value = "lunatech.infra.auth.AuthRepositoryAdapterITest#authModels")
        public void findByUsernameShouldReturnData(AuthModel authModel) {
            AuthEntity.persist(AuthEntity.from(authModel));
            assertThat(repository.findByUsername(authModel.username())).isEqualTo(Optional.of(authModel));
        }

        @Test
        public void findByUsernameShouldReturnNothing() {
            assertThat(repository.findByUsername("???")).isEqualTo(Optional.empty());
        }

    }

    @Nested
    class Save {

        @Test
        public void savedAuthShouldBeAccessible() {
            AuthModel newAuthModel = new AuthModel("test", "test", AuthModel.Role.ADMIN);

            assertThat(repository.findByUsername(newAuthModel.username())).isEqualTo(Optional.empty());
            assertDoesNotThrow(() -> repository.save(newAuthModel));
            assertThat(repository.findByUsername(newAuthModel.username())).isEqualTo(Optional.of(newAuthModel));
        }

    }

}
