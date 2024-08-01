package lunatech.services;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import lunatech.security.Access;
import lunatech.security.Action;
import lunatech.security.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class AuthServiceTest {
    @Inject AuthService authService;

    @Test
    @TestSecurity(user = "CurrentUser", roles = {Role.Names.ADMIN})
    void testAdmin() {
        assertThat(authService.userName()).isEqualTo("CurrentUser");
        assertThat(authService.access(Action.GET)).isEqualTo(Access.GLOBAL);
        assertThat(authService.access(Action.POST)).isEqualTo(Access.GLOBAL);
        assertThat(authService.access(Action.PUT)).isEqualTo(Access.GLOBAL);
        assertThat(authService.access(Action.DELETE)).isEqualTo(Access.GLOBAL);
    }

    @Test
    @TestSecurity(user = "CurrentUser", roles = {Role.Names.REGULAR})
    void testRegular() {
        assertThat(authService.userName()).isEqualTo("CurrentUser");
        assertThat(authService.access(Action.GET)).isEqualTo(Access.PERSONAL);
        assertThat(authService.access(Action.POST)).isEqualTo(Access.PERSONAL);
        assertThat(authService.access(Action.PUT)).isEqualTo(Access.PERSONAL);
        assertThat(authService.access(Action.DELETE)).isEqualTo(Access.PERSONAL);
    }

    @Test
    @TestSecurity(user = "CurrentUser")
    void testUnknown() {
        assertThat(authService.userName()).isEqualTo("CurrentUser");
        assertThat(authService.access(Action.GET)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.POST)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.PUT)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.DELETE)).isEqualTo(Access.NONE);
    }

    @Test
    @TestSecurity(user = "CurrentUser", roles = {"?"})
    void testUnknown2() {
        assertThat(authService.userName()).isEqualTo("CurrentUser");
        assertThat(authService.access(Action.GET)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.POST)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.PUT)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.DELETE)).isEqualTo(Access.NONE);
    }

    @Test
    void testNotConnected() {
        assertThat(authService.userName()).isEqualTo("");
        assertThat(authService.access(Action.GET)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.POST)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.PUT)).isEqualTo(Access.NONE);
        assertThat(authService.access(Action.DELETE)).isEqualTo(Access.NONE);
    }
}