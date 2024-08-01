package lunatech;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;
import lunatech.security.Role;
import lunatech.entities.UserEntity;
import lombok.extern.java.Log;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is executed everytime that we launch the application. We use it to load fixtures.
 */
@JBossLog
public class Startup {

    private final List<UserEntity> users = List.of(
            new UserEntity("Nicolas", "pwd", Role.Names.ADMIN, new ArrayList<>()),
            new UserEntity("Ewen", "pwd", Role.Names.REGULAR, new ArrayList<>()),
            new UserEntity("Sebastien", "pwd", Role.Names.REGULAR, new ArrayList<>())
    );

    @Transactional
    public void loadFixtures(@Observes StartupEvent evt) {
        log.info("Executing fixtures startup operation");

        UserEntity.deleteAll();
        users.forEach(u -> u.persist());
    }
}