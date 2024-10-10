package lunatech.test.resources.utils;

import io.restassured.specification.RequestSpecification;
import io.vertx.core.json.JsonObject;

import static io.restassured.RestAssured.given;

public abstract class HttpRequest {

    public static RequestSpecification withCredentials(String username, String password) {
        return given().auth().basic(username, password);
    }

    public static String toJson(Object obj) {
        return JsonObject.mapFrom(obj).toString();
    }

}
