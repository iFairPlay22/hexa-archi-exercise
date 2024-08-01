package lunatech.entities;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lunatech.security.Role;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class UserEntityTest {
  @Inject Validator validator;

  @Test
  void ensureEntityValidationSucceeds() {
    var todo = new UserEntity("<user-name>", "<password>", Role.Names.ADMIN, List.of());
    var violations = validator.validate(todo);
    assertThat(violations).isEmpty();
  }

  @Test
  void ensureEntityValidationFails1() {
    var todo = new UserEntity(null, null, Role.Names.UNKNOWN, null);
    var violations = validator.validate(todo);
    var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();

    assertThat(violationMessages).isNotEmpty();
    assertThat(violationMessages)
        .containsOnly("Username should be set", "Password should be set", "Todos should be set");
  }

  @Test
  void ensureEntityValidationFails2() {
    var todos = new ArrayList<TodoEntity>();
    todos.add(null);
    var todo = new UserEntity("???", "???", Role.Names.UNKNOWN, todos);
    var violations = validator.validate(todo);
    var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();

    assertThat(violationMessages).isNotEmpty();
    assertThat(violationMessages).containsOnly("Todos.* should not be null");
  }
}
