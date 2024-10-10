package lunatech.domain.todos.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lunatech.test.resources.fixtures.AFixture;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;

import static lunatech.domain.auth.models.AuthModelFixtures.Samples.*;

public class UserWithTodosModelFixtures extends AFixture<UserWithTodosModel, UserWithTodosModelFixtures.Params> {

    private final static TodoModelFixtures TODO_MODEL_FIXTURES = new TodoModelFixtures();

    @Override
    public @NotNull UserWithTodosModelFixtures.Params defaultParams() {
        return UserWithTodosModelFixtures.Params.builder().build();
    }

    @Override
    public @NotNull UserWithTodosModel one(@NotNull UserWithTodosModelFixtures.Params params) {
        return new UserWithTodosModel(
                params.username != null ? params.username : faker.funnyName().name(),
                params.todos != null ? params.todos : TODO_MODEL_FIXTURES.many(faker.random().nextInt(3, 6), TodoModelFixtures.Params.builder().build())
        );
    }

    @Builder
    public static class Params {
        private final @NotNull String username;
        private final @NotNull List<TodoModel> todos;
    }

    public static class Samples {

        public static final UserWithTodosModelFixtures USER_WITH_TODOS_MODEL_FIXTURES = new UserWithTodosModelFixtures();

        public static final UserWithTodosModel ADMIN_USER_WITH_TODOS_MODEL = USER_WITH_TODOS_MODEL_FIXTURES.one(
                UserWithTodosModelFixtures.Params.builder().username(ADMIN_AUTH_MODEL.username()).build()
        );

        public static final UserWithTodosModel REGULAR_USER_WITH_TODOS_MODEL = USER_WITH_TODOS_MODEL_FIXTURES.one(
                UserWithTodosModelFixtures.Params.builder().username(REGULAR_AUTH_MODEL.username()).build()
        );
        public static final UserWithTodosModel UNKNOWN_USER_WITH_TODOS_MODEL = USER_WITH_TODOS_MODEL_FIXTURES.one(
                UserWithTodosModelFixtures.Params.builder().username(UNKNOWN_AUTH_MODEL.username()).build()
        );

        public static final List<Arguments> AUTHORIZED_ACCESSES_ARGUMENTS = List.of(
                Arguments.of(ADMIN_AUTH_MODEL, ADMIN_USER_WITH_TODOS_MODEL, ADMIN_USER_WITH_TODOS_MODEL),
                Arguments.of(ADMIN_AUTH_MODEL, ADMIN_USER_WITH_TODOS_MODEL, REGULAR_USER_WITH_TODOS_MODEL),
                Arguments.of(ADMIN_AUTH_MODEL, ADMIN_USER_WITH_TODOS_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL),
                Arguments.of(REGULAR_AUTH_MODEL, REGULAR_USER_WITH_TODOS_MODEL, REGULAR_USER_WITH_TODOS_MODEL)
        );

        public static final List<Arguments> UNAUTHORIZED_ACCESSES_ARGUMENTS = List.of(
                Arguments.of(REGULAR_AUTH_MODEL, REGULAR_USER_WITH_TODOS_MODEL, ADMIN_USER_WITH_TODOS_MODEL),
                Arguments.of(REGULAR_AUTH_MODEL, REGULAR_USER_WITH_TODOS_MODEL, ADMIN_USER_WITH_TODOS_MODEL),
                Arguments.of(UNKNOWN_AUTH_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL, ADMIN_USER_WITH_TODOS_MODEL),
                Arguments.of(UNKNOWN_AUTH_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL, REGULAR_USER_WITH_TODOS_MODEL),
                Arguments.of(UNKNOWN_AUTH_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL)
        );

    }

}
