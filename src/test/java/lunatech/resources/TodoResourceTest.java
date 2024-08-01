package lunatech.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import lunatech.entities.TodoEntity;
import lunatech.entities.UserEntity;
import lunatech.security.Role;
import org.bson.types.ObjectId;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class TodoResourceTest {
  private static final TodoEntity todo =
      new TodoEntity(new ObjectId(), "title", List.of("<tag>"), false);
  private static final TodoEntity todo2 =
      new TodoEntity(new ObjectId(), "title2", List.of("<tag2>"), true);
  private static final TodoEntity todo2Update =
      new TodoEntity(todo2.todoId, "title2.1", List.of("<tag2.1>"), false);
  private static final UserEntity regularUser =
      new UserEntity("RegularUser", "<password2>", Role.Names.REGULAR, List.of());
  private static final UserEntity adminUser =
      new UserEntity("AdminUser", "<password4>", Role.Names.ADMIN, List.of());
  private static final List<UserEntity> users = List.of(regularUser, adminUser);
  private static final RegularTest regularTest = new RegularTest();
  private static final AdminTest adminTest = new AdminTest();

  @BeforeAll
  public static void setup() {
    UserEntity.deleteAll();
    users.forEach(u -> u.persistOrUpdate());
  }

  @AfterAll
  public static void cleanup() {
    UserEntity.deleteAll();
  }

  @Test
  void testAnonymousUser() {
    regularTest.failure();
    adminTest.failure();
  }

  @Test
  @TestSecurity(
      user = "RegularUser",
      roles = {Role.Names.REGULAR})
  void testRegularUser() {
    regularTest.success();
    adminTest.failure();
  }

  @Test
  @TestSecurity(
      user = "AdminUser",
      roles = {Role.Names.ADMIN})
  void testAdminUser() {
    regularTest.success();
    adminTest.success();
  }

  static class RegularTest {
    void success() {
      // Todos: []
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .isEmpty();
      getTodo(todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(todo2).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: []
      putTodo(todo2Update).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .isEmpty();
      getTodo(todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(todo2).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: []
      deleteTodo(todo2Update).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .isEmpty();
      getTodo(todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(todo2).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: [ todo_ ]
      postTodo(todo).then().statusCode(StatusCode.CREATED);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .containsOnly(todo);
      assertThat(getTodo(todo).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo);
      getTodo(todo2).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: [ todo_, todo2_ ]
      postTodo(todo2).then().statusCode(StatusCode.CREATED);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .containsOnly(todo, todo2);
      assertThat(getTodo(todo).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo);
      assertThat(getTodo(todo2).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo2);

      // Todos: [ todo_, todo2Update_ ]
      putTodo(todo2Update).then().statusCode(StatusCode.OK);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .containsOnly(todo, todo2Update);
      assertThat(getTodo(todo).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo);
      assertThat(
              getTodo(todo2Update).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo2Update);

      // Todos: [ todo_ ]
      deleteTodo(todo2Update).then().statusCode(StatusCode.NO_CONTENT);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .containsOnly(todo);
      assertThat(getTodo(todo).then().statusCode(StatusCode.OK).extract().as(TodoEntity.class))
          .isEqualTo(todo);
      getTodo(todo2Update).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: []
      deleteTodo(todo).then().statusCode(StatusCode.NO_CONTENT);
      assertThat(getAllTodos().then().statusCode(StatusCode.OK).extract().as(TodoEntity[].class))
          .isEmpty();
      getTodo(todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(todo2).then().statusCode(StatusCode.NOT_FOUND);
    }

    void failure() {
      getAllTodos().then().statusCode(StatusCode.UNAUTHORIZED);
      getTodo(todo).then().statusCode(StatusCode.UNAUTHORIZED);
      getTodo(todo2).then().statusCode(StatusCode.UNAUTHORIZED);
      postTodo(todo2).then().statusCode(StatusCode.UNAUTHORIZED);
      putTodo(todo2Update).then().statusCode(StatusCode.UNAUTHORIZED);
      deleteTodo(todo2Update).then().statusCode(StatusCode.UNAUTHORIZED);
    }

    private Response getAllTodos() {
      return given().when().get("/api/todos");
    }

    private Response getTodo(TodoEntity todo) {
      return given().when().get(String.format("/api/todos/%s", todo.todoId));
    }

    private Response postTodo(TodoEntity todo) {
      return given().contentType(ContentType.JSON).body(toJson(todo)).when().post("/api/todos");
    }

    private Response putTodo(TodoEntity todo) {
      return given().contentType(ContentType.JSON).body(toJson(todo)).when().put("/api/todos");
    }

    private Response deleteTodo(TodoEntity todo) {
      return given().when().delete(String.format("/api/todos/%s", todo.todoId));
    }
  }

  static class AdminTest {

    private static final TodoEntity todo3 =
        new TodoEntity(new ObjectId(), "title3", List.of("<tag3>"), true);
    private static final UserEntity regularUser2 =
        new UserEntity("RegularUser2", "<password2>", Role.Names.REGULAR, List.of(todo3));

    void success() {
      regularUser2.persistOrUpdate();

      // Todos: [ todo3 ]
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo3);
      getTodo(regularUser2, todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);
      ;

      // Todos: [ todo3 ]
      putTodo(regularUser2, todo2Update).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo3);
      getTodo(regularUser2, todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);
      ;

      // Todos: [ todo3 ]
      deleteTodo(regularUser2, todo2Update).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo3);
      getTodo(regularUser2, todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);

      // Todos: [ todo_, todo3 ]
      postTodo(regularUser2, todo).then().statusCode(StatusCode.CREATED);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo, todo3);
      assertThat(
              getTodo(regularUser2, todo)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);

      // Todos: [ todo_, todo2_, todo3 ]
      postTodo(regularUser2, todo2).then().statusCode(StatusCode.CREATED);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo, todo2, todo3);
      assertThat(
              getTodo(regularUser2, todo)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo);
      assertThat(
              getTodo(regularUser2, todo2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo2);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);

      // Todos: [ todo_, todo2Update_, todo3 ]
      putTodo(regularUser2, todo2Update).then().statusCode(StatusCode.OK);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo, todo2Update, todo3);
      assertThat(
              getTodo(regularUser2, todo)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo);
      assertThat(
              getTodo(regularUser2, todo2Update)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo2Update);
      assertThat(
              getTodo(regularUser2, todo3)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo3);

      // Todos: [ todo_, todo2Update_ ]
      deleteTodo(regularUser2, todo3).then().statusCode(StatusCode.NO_CONTENT);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo, todo2Update);
      assertThat(
              getTodo(regularUser2, todo)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo);
      assertThat(
              getTodo(regularUser2, todo2Update)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo2Update);
      getTodo(regularUser2, todo3).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: [ todo_ ]
      deleteTodo(regularUser2, todo2Update).then().statusCode(StatusCode.NO_CONTENT);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .containsOnly(todo);
      assertThat(
              getTodo(regularUser2, todo)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity.class))
          .isEqualTo(todo);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo3).then().statusCode(StatusCode.NOT_FOUND);

      // Todos: []
      deleteTodo(regularUser2, todo).then().statusCode(StatusCode.NO_CONTENT);
      assertThat(
              getAllTodos(regularUser2)
                  .then()
                  .statusCode(StatusCode.OK)
                  .extract()
                  .as(TodoEntity[].class))
          .isEmpty();
      getTodo(regularUser2, todo).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.NOT_FOUND);
      getTodo(regularUser2, todo3).then().statusCode(StatusCode.NOT_FOUND);

      regularUser2.delete();
    }

    void failure() {
      regularUser2.persistOrUpdate();

      getAllTodos(regularUser2).then().statusCode(StatusCode.UNAUTHORIZED);
      getTodo(regularUser2, todo).then().statusCode(StatusCode.UNAUTHORIZED);
      getTodo(regularUser2, todo2).then().statusCode(StatusCode.UNAUTHORIZED);
      getTodo(regularUser2, todo3).then().statusCode(StatusCode.UNAUTHORIZED);
      postTodo(regularUser2, todo2).then().statusCode(StatusCode.UNAUTHORIZED);
      putTodo(regularUser2, todo2Update).then().statusCode(StatusCode.UNAUTHORIZED);
      deleteTodo(regularUser2, todo2Update).then().statusCode(StatusCode.UNAUTHORIZED);

      regularUser2.delete();
    }

    private Response getAllTodos(UserEntity user) {
      return given().when().get(String.format("/api/todos?user=%s", user.username));
    }

    private Response getTodo(UserEntity user, TodoEntity todoId) {
      return given()
          .when()
          .get(String.format("/api/todos/%s?user=%s", todoId.todoId, user.username));
    }

    private Response postTodo(UserEntity user, TodoEntity todo) {
      return given()
          .contentType(ContentType.JSON)
          .body(toJson(todo))
          .when()
          .post(String.format("/api/todos?user=%s", user.username));
    }

    private Response putTodo(UserEntity user, TodoEntity todo) {
      return given()
          .contentType(ContentType.JSON)
          .body(toJson(todo))
          .when()
          .put(String.format("/api/todos?user=%s", user.username));
    }

    private Response deleteTodo(UserEntity user, TodoEntity todoId) {
      return given()
          .when()
          .delete(String.format("/api/todos/%s?user=%s", todoId.todoId, user.username));
    }
  }

  private static String toJson(Object obj) {
    return JsonObject.mapFrom(obj).toString();
  }
}
