package lunatech;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class TodoResourceTest {
    @Test
    void testTodos() {
        given()
          .when().get("/api/todos")
          .then()
             .statusCode(200)
             .body(is(List.of()));
    }

}