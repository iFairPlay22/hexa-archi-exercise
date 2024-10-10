package lunatech.application.server;

import io.quarkus.test.junit.QuarkusTest;
import lunatech.test.resources.lifecycle.mongo.WithMongoDbResourceManager;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@WithMongoDbResourceManager
class HttpServerHealthCheckITest {

    @Test
    void testHealthCheck() {
        given()
                .when()
                .get("/q/health/live")
                .then()
                .statusCode(StatusCode.OK)
                .body(containsString("Service healthy"));
    }

}
