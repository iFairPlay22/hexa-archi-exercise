package lunatech.application.server.resources.todos;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lunatech.application.server.resources.todos.requests.CreateTodoRequest;
import lunatech.application.server.resources.todos.requests.CreateTodoRequestUtils;
import lunatech.application.server.resources.todos.requests.UpdateTodoRequest;
import lunatech.application.server.resources.todos.requests.UpdateTodoRequestUtils;
import lunatech.application.server.resources.todos.responses.GetTodoResponse;
import lunatech.application.server.resources.todos.responses.GetTodoResponseUtils;
import lunatech.application.server.resources.todos.responses.GetTodosResponse;
import lunatech.domain.auth.models.AuthModel;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import lunatech.infra.auth.entities.AuthEntity;
import lunatech.infra.todos.entities.UserWithTodosEntity;
import lunatech.test.resources.lifecycle.mongo.WithMongoDbResourceManager;
import org.bson.types.ObjectId;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static lunatech.application.server.resources.todos.TodosResourceITest.Utils.concat;
import static lunatech.application.server.resources.todos.TodosResourceITest.Utils.listTodos;
import static lunatech.domain.todos.models.TodoModelFixtures.Samples.TODO_MODEL_FIXTURES;
import static lunatech.domain.todos.models.UserWithTodosModelFixtures.Samples.AUTHORIZED_ACCESSES_ARGUMENTS;
import static lunatech.domain.todos.models.UserWithTodosModelFixtures.Samples.UNAUTHORIZED_ACCESSES_ARGUMENTS;
import static lunatech.test.resources.utils.HttpRequest.toJson;
import static lunatech.test.resources.utils.HttpRequest.withCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

@QuarkusTest
@WithMongoDbResourceManager
class TodosResourceITest {

    private static List<Arguments> authorizedAccesses() {
        return AUTHORIZED_ACCESSES_ARGUMENTS;
    }

    private static List<Arguments> unauthorizedAccesses() {
        return UNAUTHORIZED_ACCESSES_ARGUMENTS;
    }

    @BeforeEach
    void beforeEach() {
        AuthEntity.deleteAll();
        UserWithTodosEntity.deleteAll();
    }

    @AllArgsConstructor
    private static class Requests {
        private final AuthModel authModel;

        private RequestSpecification base() {
            return withCredentials(authModel.username(), authModel.password());
        }

        public Response getAllTodos() {
            return base().when().get("/api/todos");
        }

        public Response getAllTodosOfUser(UserWithTodosModel user) {
            return base().when()
                    .get(String.format("/api/todos?user=%s", user.username()));
        }

        public Response getTodosByFilter(String tags) {
            return base().when()
                    .get(String.format("/api/todos?filter=%s", tags));
        }

        public Response getTodosOfUserByFilter(UserWithTodosModel user, String tags) {
            return base().when()
                    .get(String.format("/api/todos?user=%s&filter=%s", user.username(), tags));
        }

        public Response getTodoById(ObjectId todoId) {
            return base().when()
                    .get(String.format("/api/todos/%s", todoId));
        }

        public Response getTodoOfUserById(UserWithTodosModel user, ObjectId todoId) {
            return base().when()
                    .get(String.format("/api/todos/%s?user=%s", todoId, user.username()));
        }

        public Response deleteTodo(ObjectId todoId) {
            return base().when()
                    .delete(String.format("/api/todos/%s", todoId));
        }

        public Response deleteTodoOfUser(UserWithTodosModel user, ObjectId todoId) {
            return base().when()
                    .delete(String.format("/api/todos/%s?user=%s", todoId, user.username()));
        }

        public Response createTodo(CreateTodoRequest request) {
            return base().when()
                    .contentType(ContentType.JSON)
                    .body(toJson(request))
                    .post("/api/todos");
        }

        public Response createTodoOfUser(UserWithTodosModel user, CreateTodoRequest request) {
            return base().when()
                    .contentType(ContentType.JSON)
                    .body(toJson(request))
                    .post(String.format("/api/todos?user=%s", user.username()));
        }

        public Response updateTodo(ObjectId id, UpdateTodoRequest request) {
            return base().when()
                    .contentType(ContentType.JSON)
                    .body(toJson(request))
                    .put(String.format("/api/todos/%s", id));
        }

        public Response updateTodoOfUser(UserWithTodosModel user, ObjectId id, UpdateTodoRequest request) {
            return base().when()
                    .contentType(ContentType.JSON)
                    .body(toJson(request))
                    .put(String.format("/api/todos/%s?user=%s", id, user.username()));
        }
    }

    static class Utils {

        static List<UserWithTodosModel> listTodos() {
            List<UserWithTodosEntity> todos = UserWithTodosEntity.listAll();
            return todos.stream().map(UserWithTodosEntity::to).toList();
        }

        static <T> List<T> concat(List<T> a, List<T> b) {
            return Stream.concat(a.stream(), b.stream()).toList();
        }

        static <T> List<T> concat(List<T> a, T b) {
            return concat(a, List.of(b));
        }
    }

    @Nested
    class Scenarios {

        @Nested
        class PersonalTodosAccess {

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void getAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = actorUserModel.todos().get(0);
                String filter = existingTodo.title().substring(2);

                new Requests(actorAuthModel).getAllTodos()
                        .then()
                        .statusCode(StatusCode.OK)
                        .body(is(toJson(GetTodosResponse.from(actorUserModel.todos()))));

                new Requests(actorAuthModel).getTodosByFilter(filter)
                        .then()
                        .statusCode(StatusCode.OK)
                        .body(is(toJson(GetTodosResponse.from(List.of(existingTodo)))));

                new Requests(actorAuthModel).getTodoById(existingTodo.id())
                        .then()
                        .statusCode(StatusCode.OK)
                        .body(is(toJson(GetTodoResponse.from(existingTodo))));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void createAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                CreateTodoRequest request = CreateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                GetTodoResponse response = new Requests(actorAuthModel).createTodo(request)
                        .then()
                        .statusCode(StatusCode.CREATED)
                        .extract()
                        .as(GetTodoResponse.class);
                TodoModel newTodoModel = GetTodoResponseUtils.from(response);

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(actorUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), concat(user.todos(), newTodoModel)) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void deleteAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel deletedTodo = actorUserModel.todos().get(0);

                new Requests(actorAuthModel).deleteTodo(deletedTodo.id())
                        .then()
                        .statusCode(StatusCode.NO_CONTENT);

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(actorUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), user.todos().subList(1, user.todos().size())) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void updateAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = actorUserModel.todos().get(0);
                UpdateTodoRequest request = UpdateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                GetTodoResponse response = new Requests(actorAuthModel).updateTodo(existingTodo.id(), request)
                        .then()
                        .statusCode(StatusCode.OK)
                        .extract()
                        .as(GetTodoResponse.class);
                TodoModel updatedTodoModel = GetTodoResponseUtils.from(response);

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(actorUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), concat(user.todos().subList(1, user.todos().size()), updatedTodoModel)) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void getWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = actorUserModel.todos().get(0);
                String filter = existingTodo.title().substring(2);

                new Requests(actorAuthModel).getAllTodos()
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                new Requests(actorAuthModel).getTodosByFilter(filter)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                new Requests(actorAuthModel).getTodoById(existingTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void createWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                CreateTodoRequest request = CreateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                new Requests(actorAuthModel).createTodo(request)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void deleteWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel deletedTodo = actorUserModel.todos().get(0);

                new Requests(actorAuthModel).deleteTodo(deletedTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void updateWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                if (!actorUserModel.username().equals(targetUserModel.username())) return;

                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = actorUserModel.todos().get(0);
                UpdateTodoRequest request = UpdateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                new Requests(actorAuthModel).updateTodo(existingTodo.id(), request)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

        }

        @Nested
        class ExternalTodosAccess {

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void getAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();

                TodoModel existingTodo = targetUserModel.todos().get(0);
                String filter = existingTodo.title().substring(2);

                // Get all todos
                new Requests(actorAuthModel).getAllTodosOfUser(targetUserModel)
                        .then()
                        .statusCode(oneOf(StatusCode.OK))
                        .body(is(toJson(GetTodosResponse.from(targetUserModel.todos()))));

                // Get all todos with filter
                new Requests(actorAuthModel).getTodosOfUserByFilter(targetUserModel, filter)
                        .then()
                        .statusCode(oneOf(StatusCode.OK))
                        .body(is(toJson(GetTodosResponse.from(List.of(existingTodo)))));

                // Get _todo by id
                new Requests(actorAuthModel).getTodoOfUserById(targetUserModel, existingTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.OK))
                        .body(is(toJson(GetTodoResponse.from(existingTodo))));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void createAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                CreateTodoRequest request = CreateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                GetTodoResponse response = new Requests(actorAuthModel).createTodoOfUser(targetUserModel, request)
                        .then()
                        .statusCode(oneOf(StatusCode.CREATED))
                        .extract()
                        .as(GetTodoResponse.class);
                TodoModel newTodoModel = GetTodoResponseUtils.from(response);

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(targetUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), concat(user.todos(), newTodoModel)) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void deleteAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel deletedTodo = targetUserModel.todos().get(0);

                new Requests(actorAuthModel).deleteTodoOfUser(targetUserModel, deletedTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.NO_CONTENT));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(targetUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), user.todos().subList(1, user.todos().size())) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#authorizedAccesses")
            public void updateAuthorizedTodos(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = targetUserModel.todos().get(0);
                UpdateTodoRequest request = UpdateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                GetTodoResponse response = new Requests(actorAuthModel).updateTodoOfUser(targetUserModel, existingTodo.id(), request)
                        .then()
                        .statusCode(oneOf(StatusCode.OK))
                        .extract()
                        .as(GetTodoResponse.class);
                TodoModel updatedTodoModel = GetTodoResponseUtils.from(response);

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(
                        initialState.stream()
                                .map(user ->
                                        user.username().equals(targetUserModel.username()) ?
                                                new UserWithTodosModel(user.username(), concat(user.todos().subList(1, user.todos().size()), updatedTodoModel)) :
                                                user
                                )
                                .toList()
                );
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void getWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = targetUserModel.todos().get(0);
                String filter = existingTodo.title().substring(2);

                new Requests(actorAuthModel).getAllTodosOfUser(targetUserModel)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                new Requests(actorAuthModel).getTodosOfUserByFilter(targetUserModel, filter)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                new Requests(actorAuthModel).getTodoOfUserById(targetUserModel, existingTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void createWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                CreateTodoRequest request = CreateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                new Requests(actorAuthModel).createTodoOfUser(targetUserModel, request)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void deleteWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel deletedTodo = targetUserModel.todos().get(0);

                new Requests(actorAuthModel).deleteTodoOfUser(targetUserModel, deletedTodo.id())
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

            @ParameterizedTest
            @MethodSource("lunatech.application.server.resources.todos.TodosResourceITest#unauthorizedAccesses")
            public void updateWhenUnauthorized(AuthModel actorAuthModel, UserWithTodosModel actorUserModel, UserWithTodosModel targetUserModel) {
                AuthEntity.persist(AuthEntity.from(actorAuthModel));
                UserWithTodosEntity.persist(UserWithTodosEntity.from(targetUserModel));

                List<UserWithTodosModel> initialState = listTodos();
                TodoModel existingTodo = targetUserModel.todos().get(0);
                UpdateTodoRequest request = UpdateTodoRequestUtils.from(TODO_MODEL_FIXTURES.one());

                new Requests(actorAuthModel).updateTodoOfUser(targetUserModel, existingTodo.id(), request)
                        .then()
                        .statusCode(oneOf(StatusCode.FORBIDDEN, StatusCode.UNAUTHORIZED));

                List<UserWithTodosModel> finalState = listTodos();
                assertThat(finalState).containsAll(initialState);
            }

        }
    }
}
