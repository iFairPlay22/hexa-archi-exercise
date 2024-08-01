package lunatech.services;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import lunatech.security.Role;
import lunatech.entities.TodoEntity;
import lunatech.entities.UserEntity;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@QuarkusTest
public class UserServiceTest {
  // Tested directly in TodoResourceTest
}
