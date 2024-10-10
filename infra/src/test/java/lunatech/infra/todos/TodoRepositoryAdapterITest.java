package lunatech.infra.todos;

import io.quarkus.test.junit.QuarkusTest;
import lunatech.domain.todos.TodoRepositoryPort;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import lunatech.domain.todos.models.UserWithTodosModelFixtures;
import lunatech.infra.todos.entities.UserWithTodosEntity;
import lunatech.test.resources.lifecycle.mongo.WithMongoDbResourceManager;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;

import static lunatech.domain.todos.models.UserWithTodosModelFixtures.Samples.USER_WITH_TODOS_MODEL_FIXTURES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
@WithMongoDbResourceManager
public class TodoRepositoryAdapterITest {

    private static final TodoRepositoryPort repository = new TodoRepositoryAdapter();

    private static List<Arguments> userWithTodosModels() {
        return USER_WITH_TODOS_MODEL_FIXTURES.many(10, UserWithTodosModelFixtures.Params.builder().build()).stream().map(Arguments::of).toList();
    }

    @BeforeEach
    public void beforeEach() {
        UserWithTodosEntity.deleteAll();
    }

    @Nested
    class FindUserByUsername {

        @ParameterizedTest
        @MethodSource(value = "lunatech.infra.todos.TodoRepositoryAdapterITest#userWithTodosModels")
        public void findUserByMatchingUsername(UserWithTodosModel userWithTodosModel) {
            UserWithTodosEntity.persist(UserWithTodosEntity.from(userWithTodosModel));
            assertThat(repository.findByUserName(userWithTodosModel.username())).isEqualTo(Optional.of(userWithTodosModel));
        }

        @Test
        public void findUserByNotMatchingUsername() {
            assertThat(repository.findByUserName("???")).isEqualTo(Optional.empty());
        }

    }

    @Nested
    class FindUserByUsernameAndId {

        @ParameterizedTest
        @MethodSource(value = "lunatech.infra.todos.TodoRepositoryAdapterITest#userWithTodosModels")
        public void findUserByUsernameAndMatchingId(UserWithTodosModel userWithTodosModel) {
            UserWithTodosEntity.persist(UserWithTodosEntity.from(userWithTodosModel));
            userWithTodosModel.todos().forEach(todoModel ->
                    assertThat(repository.findByUserNameAndId(userWithTodosModel.username(), todoModel.id())).isEqualTo(Optional.of(todoModel))
            );
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.infra.todos.TodoRepositoryAdapterITest#userWithTodosModels")
        public void findUserByUsernameAndNotMatchingId(UserWithTodosModel userWithTodosModel) {
            UserWithTodosEntity.persist(UserWithTodosEntity.from(userWithTodosModel));
            assertThat(repository.findByUserNameAndId(userWithTodosModel.username(), new ObjectId())).isEqualTo(Optional.empty());
        }

        @Test
        public void findUserByNotMatchingUsernameAndId() {
            assertThat(repository.findByUserNameAndId("???", new ObjectId())).isEqualTo(Optional.empty());
        }

    }

    @Nested
    class Save {

        @Test
        public void saveShouldCreateUser() {
            UserWithTodosModel userWithTodos = new UserWithTodosModel("test", List.of(new TodoModel(new ObjectId(), "test", List.of("test"), false)));
            assertDoesNotThrow(() -> repository.save(userWithTodos));
            assertThat(UserWithTodosEntity.listAll()).containsAll(List.of(UserWithTodosEntity.from(userWithTodos)));
        }

        @Test
        public void saveShouldUpdateUser() {
            UserWithTodosModel userWithTodos = new UserWithTodosModel("test", List.of(new TodoModel(new ObjectId(), "test", List.of("test"), false)));
            UserWithTodosModel updatedUserWithTodos = new UserWithTodosModel("test", List.of());
            UserWithTodosEntity.persist(UserWithTodosEntity.from(userWithTodos));
            assertDoesNotThrow(() -> repository.save(updatedUserWithTodos));
            assertThat(UserWithTodosEntity.listAll()).isEqualTo(List.of(UserWithTodosEntity.from(updatedUserWithTodos)));
        }
    }
}
