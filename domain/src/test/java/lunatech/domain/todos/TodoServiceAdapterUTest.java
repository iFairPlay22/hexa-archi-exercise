package lunatech.domain.todos;

import lunatech.domain.todos.payloads.TodoModelCreatePayloadUtils;
import lunatech.domain.todos.payloads.TodoModelUpdatePayloadUtils;
import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.AuthServiceAdapter;
import lunatech.domain.auth.AuthServicePort;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.auth.models.AuthModel;
import lunatech.domain.mocks.AuthRepositoryMock;
import lunatech.domain.mocks.TodoRepositoryMock;
import lunatech.domain.todos.exceptions.TodoNotFoundException;
import lunatech.domain.todos.exceptions.UserWithTodosNotFoundException;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;

import static lunatech.domain.auth.models.AuthModelFixtures.Samples.*;
import static lunatech.domain.todos.models.TodoModelFixtures.Samples.TODO_MODEL_FIXTURES;
import static lunatech.domain.todos.models.UserWithTodosModelFixtures.Samples.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoServiceAdapterUTest {

    // Repositories
    private final AuthRepositoryPort spiedAuthRepository = spy(
            new AuthRepositoryMock(ADMIN_AUTH_MODEL, REGULAR_AUTH_MODEL, UNKNOWN_AUTH_MODEL)
    );
    private final TodoRepositoryMock spiedTodoRepository = spy(
            new TodoRepositoryMock(ADMIN_USER_WITH_TODOS_MODEL, REGULAR_USER_WITH_TODOS_MODEL, UNKNOWN_USER_WITH_TODOS_MODEL)
    );
    // Services
    private final AuthServicePort authService = new AuthServiceAdapter(spiedAuthRepository);
    private final TodoServicePort todosService = new TodoServiceAdapter(authService, spiedTodoRepository);

    private static List<Arguments> authorizedAccesses() {
        return AUTHORIZED_ACCESSES_ARGUMENTS;
    }

    private static List<Arguments> unauthorizedAccesses() {
        return UNAUTHORIZED_ACCESSES_ARGUMENTS;
    }

    @Nested
    class FetchAllTodos {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchAllTodosOfAnExistingUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) throws UnauthorizedActionException, UserWithTodosNotFoundException {
            assertThat(todosService.fetchTodos(actorModel.username(), targetModel.username())).isEqualTo(targetModel.todos());
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchAllTodosOfAUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                assertThrows(UserWithTodosNotFoundException.class, () -> todosService.fetchTodos(actorModel.username(), "???"));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodos(actorModel.username(), "???"));
            }
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void fetchAllTodosWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodos(actorModel.username(), targetModel.username()));
            verify(spiedTodoRepository, never()).save(any());
        }

    }

    @Nested
    class FetchTodosByFilter {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodosByFilterOfAnExistingUserAndMatchingFilter(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) throws UserWithTodosNotFoundException, UnauthorizedActionException {
            TodoModel todoModel = targetModel.todos().get(0);
            assertThat(todosService.fetchTodosByFilter(actorModel.username(), targetModel.username(), todoModel.title())).isEqualTo(List.of(todoModel));
            assertThat(todosService.fetchTodosByFilter(actorModel.username(), targetModel.username(), todoModel.title().substring(2))).isEqualTo(List.of(todoModel));
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodosByFilterOfAnExistingUserAndNotMatchingFilter(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) throws UserWithTodosNotFoundException, UnauthorizedActionException {
            assertThat(todosService.fetchTodosByFilter(actorModel.username(), targetModel.username(), "??????")).isEqualTo(List.of());
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodosByFilterOfAUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            TodoModel todoModel = actorModel.todos().get(0);
            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                assertThrows(UserWithTodosNotFoundException.class, () -> todosService.fetchTodosByFilter(actorModel.username(), "???", todoModel.title()));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodosByFilter(actorModel.username(), "???", todoModel.title()));
            }
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void fetchTodosByFilterWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel todoModel = targetModel.todos().get(0);
            assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodosByFilter(actorModel.username(), targetModel.username(), todoModel.title()));
            verify(spiedTodoRepository, never()).save(any());
        }
    }

    @Nested
    class FetchTodoById {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodoByIdOfExistingUserAndMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) throws UnauthorizedActionException, TodoNotFoundException {
            TodoModel todoModel = targetModel.todos().get(0);
            assertThat(todosService.fetchTodoWithId(actorModel.username(), targetModel.username(), todoModel.id())).isEqualTo(todoModel);
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodoByIdOfExistingUserAndNotMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            assertThrows(TodoNotFoundException.class, () -> todosService.fetchTodoWithId(actorModel.username(), targetModel.username(), new ObjectId()));
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void fetchTodoByIdOfUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                assertThrows(TodoNotFoundException.class, () -> todosService.fetchTodoWithId(actorModel.username(), "???", new ObjectId()));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodoWithId(actorModel.username(), "???", new ObjectId()));
            }
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void fetchTodoByIdWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel todoModel = targetModel.todos().get(0);
            assertThrows(UnauthorizedActionException.class, () -> todosService.fetchTodoWithId(actorModel.username(), targetModel.username(), todoModel.id()));
            verify(spiedTodoRepository, never()).save(any());
        }
    }

    @Nested
    class AddTodo {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void addTodoOfExistingUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModelCreatePayload createPayload = TodoModelCreatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            TodoModel createdTodoModel = assertDoesNotThrow(() -> todosService.addTodo(actorModel.username(), targetModel.username(), createPayload));
            assertThat(TodoModelCreatePayloadUtils.from(createdTodoModel)).isEqualTo(createPayload);
            verify(spiedTodoRepository, times(1)).save(argThat(u -> u.todos().contains(createdTodoModel)));
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void addTodoOfUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            TodoModelCreatePayload createPayload = TodoModelCreatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                TodoModel createdTodoModel = assertDoesNotThrow(() -> todosService.addTodo(actorModel.username(), UUID.randomUUID().toString(), createPayload));
                assertThat(TodoModelCreatePayloadUtils.from(createdTodoModel)).isEqualTo(createPayload);
                verify(spiedTodoRepository, times(1)).save(argThat(u -> u.todos().contains(createdTodoModel)));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.addTodo(actorModel.username(), UUID.randomUUID().toString(), createPayload));
                verify(spiedTodoRepository, never()).save(any());
            }
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void addTodoWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModelCreatePayload createPayload = TodoModelCreatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            assertThrows(UnauthorizedActionException.class, () -> todosService.addTodo(actorModel.username(), targetModel.username(), createPayload));
            verify(spiedTodoRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateTodo {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void updateTodoOfExistingUserAndMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel oldTodoModel = targetModel.todos().get(0);
            TodoModelUpdatePayload updatePayload = TodoModelUpdatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            TodoModel updatedTodoModel = assertDoesNotThrow(() -> todosService.updateTodo(actorModel.username(), targetModel.username(), oldTodoModel.id(), updatePayload));
            assertThat(TodoModelUpdatePayloadUtils.from(updatedTodoModel)).isEqualTo(updatePayload);
            verify(spiedTodoRepository, times(1)).save(argThat(u -> u.todos().contains(updatedTodoModel)));
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void updateTodoUnknownUserAndNotMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModelUpdatePayload updatePayload = TodoModelUpdatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            assertThrows(TodoNotFoundException.class, () -> todosService.updateTodo(actorModel.username(), targetModel.username(), new ObjectId(), updatePayload));
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void updateTodoOfUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            TodoModel oldTodoModel = actorModel.todos().get(0);
            TodoModelUpdatePayload updatePayload = TodoModelUpdatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                assertThrows(TodoNotFoundException.class, () -> todosService.updateTodo(actorModel.username(), "???", oldTodoModel.id(), updatePayload));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.updateTodo(actorModel.username(), "???", oldTodoModel.id(), updatePayload));
            }
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void updateTodoWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel oldTodoModel = targetModel.todos().get(0);
            TodoModelUpdatePayload updatePayload = TodoModelUpdatePayloadUtils.from(TODO_MODEL_FIXTURES.one());
            assertThrows(UnauthorizedActionException.class, () -> todosService.updateTodo(actorModel.username(), targetModel.username(), oldTodoModel.id(), updatePayload));
            verify(spiedTodoRepository, never()).save(any());
        }
    }

    @Nested
    class RemoveTodo {

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void removeTodoWhenExistingUserAndMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel oldTodoModel = targetModel.todos().get(0);
            assertDoesNotThrow(() -> todosService.removeTodo(actorModel.username(), targetModel.username(), oldTodoModel.id()));
            verify(spiedTodoRepository, times(1)).save(argThat(u -> !u.todos().contains(oldTodoModel)));
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void removeTodoWhenExistingUserAndNotMatchingId(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            assertThrows(TodoNotFoundException.class, () -> todosService.removeTodo(actorModel.username(), targetModel.username(), new ObjectId()));
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#authorizedAccesses")
        void removeTodoWhenUnknownUser(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            if (!actorModel.username().equals(targetModel.username())) return;

            if (actorModel.username().equals(ADMIN_USER_WITH_TODOS_MODEL.username())) {
                assertThrows(TodoNotFoundException.class, () -> todosService.removeTodo(actorModel.username(), "???", new ObjectId()));
            } else {
                assertThrows(UnauthorizedActionException.class, () -> todosService.removeTodo(actorModel.username(), "???", new ObjectId()));
            }
            verify(spiedTodoRepository, never()).save(any());
        }

        @ParameterizedTest
        @MethodSource(value = "lunatech.domain.adapters.todos.TodoServiceAdapterUTest#unauthorizedAccesses")
        void removeTodoWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorModel, UserWithTodosModel targetModel) {
            TodoModel oldTodoModel = targetModel.todos().get(0);
            assertThrows(UnauthorizedActionException.class, () -> todosService.removeTodo(actorModel.username(), targetModel.username(), oldTodoModel.id()));
            verify(spiedTodoRepository, never()).save(any());
        }
    }
}
