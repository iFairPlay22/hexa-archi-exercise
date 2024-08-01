package lunatech.security;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class RoleTest {

  @Test
  void ensureToStringWorksProperly() {
    assertThat(Role.ADMIN.toString()).isEqualTo(Role.Names.ADMIN);
    assertThat(Role.REGULAR.toString()).isEqualTo(Role.Names.REGULAR);
    assertThat(Role.UNKNOWN.toString()).isEqualTo(Role.Names.UNKNOWN);
  }

  @Test
  void ensureToFromStringWorksProperly() {
    assertThat(Role.fromString(Role.Names.ADMIN)).isEqualTo(Role.ADMIN);
    assertThat(Role.fromString(Role.Names.REGULAR)).isEqualTo(Role.REGULAR);
    assertThat(Role.fromString(Role.Names.UNKNOWN)).isEqualTo(Role.UNKNOWN);
    assertThat(Role.fromString("?")).isEqualTo(Role.UNKNOWN);
  }
}
