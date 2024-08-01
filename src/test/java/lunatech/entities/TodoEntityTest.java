package lunatech.entities;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TodoEntityTest {
  @Inject Validator validator;

  @Test
  void ensureEntityValidationSucceeds() {
    var todo =
        new TodoEntity(
            new ObjectId(), "<todo-title>", List.of("<tag-name-1>", "<tag-name-2>"), false);
    var violations = validator.validate(todo);
    assertThat(violations).isEmpty();
  }

  @Test
  void ensureEntityValidationFails1() {
    var todo = new TodoEntity(null, null, null, null);
    var violations = validator.validate(todo);
    var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();

    assertThat(violationMessages).isNotEmpty();
    assertThat(violationMessages)
        .containsOnly(
            "Title should be set",
            "Tags should be set",
            "Done should be set",
            "Todo id should be set");
  }

  @Test
  void ensureEntityValidationFails2() {
    var todo = new TodoEntity(new ObjectId(), "?", List.of("?"), false);
    var violations = validator.validate(todo);
    var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();

    assertThat(violationMessages).isNotEmpty();
    assertThat(violationMessages)
        .containsOnly("Title length should be between 3 and 30 characters");
  }
}
