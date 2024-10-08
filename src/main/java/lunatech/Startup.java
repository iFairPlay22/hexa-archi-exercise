package lunatech;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import lunatech.security.Role;
import lunatech.entities.UserEntity;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is executed everytime that we launch the application. We use it to load fixtures.
 */
@Singleton
public class Startup {
    private static final Logger logger = Logger.getLogger(Startup.class);

    private final List<UserEntity> users = List.of(
            new UserEntity("Nicolas", "pwd", Role.ADMIN, new ArrayList<>()),
            new UserEntity("Ewen", "pwd", Role.REGULAR, new ArrayList<>()),
            new UserEntity("Sebastien", "pwd", Role.REGULAR, new ArrayList<>())
    );

    @Transactional
    public void loadFixtures(@Observes StartupEvent evt) {
        logger.info("Executing fixtures startup operation");

        UserEntity.deleteAll();
        users.forEach(u -> u.persist());
    }
}