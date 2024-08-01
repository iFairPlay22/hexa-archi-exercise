package lunatech;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import lunatech.entities.TodoEntity;
import lunatech.entities.UserEntity;
import lunatech.security.Role;
import lunatech.services.UserService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
public class StartupTest {
    @Inject Startup startup;

    @Test
    @TestSecurity(user = "CurrentUser", roles = {Role.Names.ADMIN})
    void checkAdminActions() {
        startup.loadFixtures(new StartupEvent());

        List<UserEntity> users = UserEntity.listAll();

        assertThat(users).hasSize(3);
        assertThat(users.stream().filter(u -> u.role.equals(Role.Names.ADMIN))).hasSize(1);
        assertThat(users.stream().filter(u -> u.role.equals(Role.Names.REGULAR))).hasSize(2);
        assertThat(users.stream().filter(u -> u.role.equals(Role.Names.UNKNOWN))).hasSize(0);
    }

}
