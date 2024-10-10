package lunatech.test.resources.lifecycle.mongo;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.UUID;

public class MongoDbResourceManager implements QuarkusTestResourceLifecycleManager {
    private final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.10"));

    @Override
    public Map<String, String> start() {
        mongoDBContainer.start();

        return Map.of(
                "quarkus.mongodb.connection-string", mongoDBContainer.getConnectionString(),
                "quarkus.mongodb.database", String.format("test_%s", UUID.randomUUID())
        );
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}
