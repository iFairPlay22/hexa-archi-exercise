package lunatech.test.resources.lifecycle.mongo;

import io.quarkus.test.common.WithTestResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@WithTestResource(MongoDbResourceManager.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithMongoDbResourceManager {
    /**
     * Start it with HTTPS
     */
    boolean https() default false;

    /**
     * Start it in CRUD mode
     */
    boolean crud() default true;

    /**
     * Port to use, defaults to any available port
     */
    int port() default 0;
}